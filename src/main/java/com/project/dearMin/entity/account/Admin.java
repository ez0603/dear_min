package com.project.dearMin.entity.account;

import com.project.dearMin.security.PrincipalUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    private int adminId;
    private String adminName;
    private String username;
    private String password;
    private String email;
    private LocalDate createDate;
    private LocalDate updateDate;

    public PrincipalUser toPrincipalUser() {
        return PrincipalUser.builder()
                .adminId(adminId)
                .adminName(adminName)
                .username(username)
                .email(email)
                .build();
    }
}
