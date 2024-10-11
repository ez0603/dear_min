package com.project.dearMin.dto.account.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class EditPasswordReqDto {
    @NotBlank
    private String oldPassword;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{7,128}$", message = "비밀번호는 영문자와 숫자를 포함한 7 ~ 128자리 형식이어야 합니다.")
    private String newPassword;
    @NotBlank
    private String newPasswordCheck;
}
