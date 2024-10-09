package com.project.dearMin.controller.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminFindPasswordReqDto {
    private String username;
    private String email;
}
