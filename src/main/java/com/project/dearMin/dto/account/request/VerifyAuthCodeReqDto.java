package com.project.dearMin.dto.account.request;

import lombok.Data;

@Data
public class VerifyAuthCodeReqDto {
    private String email;
    private String authCode;
}
