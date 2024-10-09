package com.project.dearMin.controller.account;

import com.project.dearMin.aop.annotation.ParamsPrintAspect;
import com.project.dearMin.aop.annotation.ValidAspect;
import com.project.dearMin.dto.account.request.EditPasswordReqDto;
import com.project.dearMin.security.PrincipalUser;
import com.project.dearMin.service.admin.AdminAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/account/admin")
public class AdminAccountController {

    @Autowired
    private AdminAccountService adminAccountService;

    @GetMapping("/principal")
    public ResponseEntity<?> getAdminPrincipal(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof PrincipalUser)) {
            throw new IllegalStateException("Authenticated principal is not an admin");
        }

        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();
        return ResponseEntity.ok(principalUser);
    }

    @ParamsPrintAspect
    @ValidAspect
    @PutMapping("/password")
    public ResponseEntity<?> editPassword(@Valid @RequestBody EditPasswordReqDto editPasswordReqDto, BindingResult bindingResult) {
        adminAccountService.editPassword(editPasswordReqDto);
        return ResponseEntity.ok(true);
    }

    @GetMapping("/id")
    public ResponseEntity<?> getUserName(@RequestParam(value = "adminName") String adminName, @RequestParam(value = "email")String email) {
        return ResponseEntity.ok(adminAccountService.findAccountByNameAndEmail(adminName, email));
    }

}