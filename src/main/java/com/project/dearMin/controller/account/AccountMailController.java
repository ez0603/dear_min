package com.project.dearMin.controller.account;

import com.project.dearMin.aop.annotation.ParamsPrintAspect;
import com.project.dearMin.dto.account.request.AdminFindPasswordReqDto;
import com.project.dearMin.dto.account.request.AdminSearchUserNameReqDto;
import com.project.dearMin.dto.account.request.VerifyAuthCodeReqDto;
import com.project.dearMin.entity.account.Admin;
import com.project.dearMin.service.admin.AccountMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/mail")
public class AccountMailController {

    @Autowired
    AccountMailService accountMailService;

    // 메일에 인증번호 보내기
    @PostMapping("/{email}/send/authenticate")
    @ResponseBody
    public ResponseEntity<?> sendAuthenticate(HttpServletRequest request, @PathVariable String email) {
        request.getSession().setAttribute("timer", new Date());
        return ResponseEntity.ok(accountMailService.sendAuthMail(email));
    }

    // 인증번호가 유효한지
    @PostMapping("/verify/authenticate")
    public ResponseEntity<?> verifyAuthCode(@RequestBody VerifyAuthCodeReqDto verifyAuthCodeReqDto) {
        Map<String, String> resultMap = accountMailService.verifyEmailCode(verifyAuthCodeReqDto.getEmail(), verifyAuthCodeReqDto.getAuthCode());
        String status = resultMap.get("status");
        String message = resultMap.get("message");

        if("success".equals(status)) {
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
        }
    }

    // 관리자 아이디 찾기
    @PostMapping("/send/admin/id")
    public ResponseEntity<?> send(HttpServletRequest request, @RequestBody AdminSearchUserNameReqDto adminSearchUserNameReqDto) {
        request.getSession().setAttribute("timer", new Date());

        // 이름과 이메일로 관리자 정보 찾기
        Admin admin = accountMailService.findAccountAdminByNameAndEmail(adminSearchUserNameReqDto.getAdminName(), adminSearchUserNameReqDto.getEmail());

        // 관리자가 없는 경우 에러 반환
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 회원정보가 없습니다.");
        }

        // 일치하는 회원이 있을 경우에만 메일 발송
        boolean isMailSent = accountMailService.searchAdminAccountByMail(admin);

        if (isMailSent) {
            return ResponseEntity.ok("아이디가 성공적으로 전송되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("아이디 전송에 실패했습니다.");
        }
    }


    // 관리자 비밀번호 찾기
    @ParamsPrintAspect
    @PostMapping("/send/temporary/admin/password")
    public ResponseEntity<?> sendAdminTemporaryPassword(HttpServletRequest request, @RequestBody AdminFindPasswordReqDto adminFindPasswordReqDto) {
        request.getSession().setAttribute("timer", new Date());
        Admin admin = accountMailService.findAccountByAdminNameAndEmail(adminFindPasswordReqDto.getUsername(), adminFindPasswordReqDto.getEmail());
        if(admin == null) {
            throw new UsernameNotFoundException("일치하는 회원정보가 없습니다.");
        }

        if (admin != null && accountMailService.sendTemporaryAdminPassword(admin)) {
            return ResponseEntity.ok("임시 비밀번호가 성공적으로 전송되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("임시 비밀번호 전송에 실패했습니다.");
        }
    }
}