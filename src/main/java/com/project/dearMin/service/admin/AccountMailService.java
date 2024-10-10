package com.project.dearMin.service.admin;

import com.project.dearMin.entity.account.Admin;
import com.project.dearMin.repository.AdminMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class AccountMailService {

    private static final long AUTH_CODE_EXPIRATION = 60 * 3L; // 3 minutes
    private static final String AUTH_SUBJECT = "[DearMin] 계정 메일 인증";
    private static final String PASSWORD_RESET_SUBJECT = "임시 비밀번호 발급";
    private static final String ACCOUNT_FIND_SUBJECT = "[DearMin] 계정 찾기";

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.address}")
    private String fromMailAddress;

    private static final Logger logger = LoggerFactory.getLogger(AccountMailService.class);

    private final Map<String, String> emailAuthCodeMap = new ConcurrentHashMap<>();
    private final Map<String, Long> emailAuthCodeExpiryMap = new ConcurrentHashMap<>();

    @Autowired
    private AdminMapper adminMapper;

    private String createAuthCode() {
        int leftLimit = 48; // number '0'
        int rightLimit = 122; // alphabet 'z'
        int targetStringLength = 6;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private void sendEmail(String to, String subject, String content) throws MessagingException {
        if (fromMailAddress == null || fromMailAddress.isEmpty()) {
            throw new IllegalArgumentException("발신자 이메일 주소가 설정되지 않았습니다.");
        }
        logger.info("Sending email to: {}", to);

        JavaMailSenderImpl mailSenderImpl = (JavaMailSenderImpl) javaMailSender;
        logger.info("SMTP Host: {}", mailSenderImpl.getJavaMailProperties().getProperty("mail.smtp.host"));
        logger.info("SMTP Port: {}", mailSenderImpl.getJavaMailProperties().getProperty("mail.smtp.port"));
        logger.info("From Address: {}", fromMailAddress);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
        helper.setSubject(subject);
        helper.setFrom(fromMailAddress);
        helper.setTo(to);
        mimeMessage.setText(content, "utf-8", "html");
        javaMailSender.send(mimeMessage);
    }

    public boolean sendAuthMail(String email) {
        String authCode = createAuthCode();
        String mailContent = "<div><h1>DearMin</h1><div><h3>인증번호는 " + authCode + "입니다</h3></div></div>";
        try {
            sendEmail(email, AUTH_SUBJECT, mailContent);
            emailAuthCodeMap.put(email, authCode);
            emailAuthCodeExpiryMap.put(email, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(AUTH_CODE_EXPIRATION));
            return true;
        } catch (MessagingException e) {
            logger.error("Failed to send authentication email", e);
            return false;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid email configuration", e);
            return false;
        }
    }

    public Admin findAccountAdminByNameAndEmail(String adminName, String email) {
        return adminMapper.findAccountByNameAndEmail(adminName, email);
    }

    public Map<String, String> verifyEmailCode(String email, String code) {
        Long expiryTime = emailAuthCodeExpiryMap.get(email);
        if (expiryTime == null || System.currentTimeMillis() > expiryTime) {
            emailAuthCodeMap.remove(email);
            emailAuthCodeExpiryMap.remove(email);
            return Map.of("status", "fail", "message", "인증 시간을 초과하였습니다.");
        }

        String authCode = emailAuthCodeMap.get(email);
        if (authCode != null && Objects.equals(authCode, code)) {
            emailAuthCodeMap.remove(email);
            emailAuthCodeExpiryMap.remove(email);
            return Map.of("status", "success", "message", "이메일 인증에 성공하였습니다.");
        } else {
            return Map.of("status", "fail", "message", "인증번호가 일치하지 않습니다.");
        }
    }

    public boolean searchAdminAccountByMail(Admin admin) {
        if (admin == null) return false;

        String mailContent = "<div><h1>DearMin</h1><div><h3>귀하의 아이디는 " + admin.getAdminName() + "입니다</h3></div></div>";
        try {
            sendEmail(admin.getEmail(), ACCOUNT_FIND_SUBJECT, mailContent);
            return true;
        } catch (MessagingException e) {
            logger.error("Failed to send account search email", e);
            return false;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid email configuration", e);
            return false;
        }
    }

    public Admin findAccountByAdminNameAndEmail(String username, String email) {
        return adminMapper.findAccountByUserNameAndEmail(username, email);
    }

    public boolean sendTemporaryAdminPassword(Admin admin) {
        if (admin == null) return false;

        String temporaryPassword = generateTemporaryPassword();
        String encodedPassword = passwordEncoder.encode(temporaryPassword);
        String mailContent = "<div><h1>DearMin</h1><div><p>안녕하세요, " + admin.getAdminName() + "님!</p>"
                + "<p>임시 비밀번호는 다음과 같습니다: <strong>" + temporaryPassword + "</strong></p>"
                + "<p>로그인 후에 비밀번호를 변경해주세요.</p></div></div>";
        try {
            sendEmail(admin.getEmail(), PASSWORD_RESET_SUBJECT, mailContent);
            adminMapper.updateAdminAccountTemporaryPw(admin.getAdminId(), encodedPassword);
            return true;
        } catch (MessagingException e) {
            logger.error("Failed to send temporary password email", e);
            return false;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid email configuration", e);
            return false;
        }
    }

    private String generateTemporaryPassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
}
