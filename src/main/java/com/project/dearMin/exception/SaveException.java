package com.project.dearMin.exception;

import lombok.Getter;

import java.util.Map;

public class SaveException extends RuntimeException{

    @Getter
    Map<String, String> errorMap;
    public SaveException(Map<String, String> errorMap) {
        super("데이터 저장 오류.");
        this.errorMap = errorMap;
    }

    public Map<String, String> getErrorMap() {
        return errorMap;
    }
}
