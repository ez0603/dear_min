package com.project.dearMin.service.admin;

import com.project.dearMin.dto.account.request.AdminSignupReqDto;
import com.project.dearMin.entity.account.Admin;
import com.project.dearMin.exception.SaveException;
import com.project.dearMin.jwt.JwtProvider;
import com.project.dearMin.repository.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AdminAuthService {

    @Autowired
    AdminMapper adminMapper;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public void adminSignup(AdminSignupReqDto adminSignupReqDto) {
        System.out.println("서비스 진입");
        int successCount = 0;
        Admin admin = adminSignupReqDto.toEntity(passwordEncoder);
        System.out.println("엔티티 생성 완료: " + admin);

        successCount += adminMapper.saveAdmin(admin);
        System.out.println("성공 카운트: " + successCount);

        if(successCount < 1) {
            throw new SaveException(Map.of("adminSignup 오류", "정상적으로 회원가입이 되지 않았습니다."));
        }

        System.out.println("회원가입 성공");
    }


    public String adminSignin(AdminSignupReqDto adminSignupReqDto) {
        Admin admin = adminMapper.findAdminByUsername(adminSignupReqDto.getUsername());
        if( admin == null) {
            throw new UsernameNotFoundException("사용자 정보를 확인하세요");
        }
        if(!passwordEncoder.matches(adminSignupReqDto.getPassword(), admin.getPassword())) {
            throw new BadCredentialsException("사용자 정보를 확인하세요");
        }
        System.out.println(123);
        return jwtProvider.generateToken(admin);
    }
}
