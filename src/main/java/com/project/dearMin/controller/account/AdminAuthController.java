package com.project.dearMin.controller.account;

import com.project.dearMin.aop.annotation.ValidAspect;
import com.project.dearMin.dto.account.request.AdminSignupReqDto;
import com.project.dearMin.service.admin.AdminAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    @Autowired
    AdminAuthService adminAuthService;

    @ValidAspect
    @PostMapping("/signup")
    public ResponseEntity<?> adminSignup(@Valid @RequestBody AdminSignupReqDto adminSignupReqDto, BindingResult bindingResult) {
        System.out.println("컨트롤러 진입");
        adminAuthService.adminSignup(adminSignupReqDto);
        System.out.println("컨트롤러 진입 끝");
        return ResponseEntity.created(null).body(true);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> adminSignin(@RequestBody AdminSignupReqDto adminSignupReqDto) {
        System.out.println(123);
        return ResponseEntity.ok(adminAuthService.adminSignin(adminSignupReqDto));
    }

}
