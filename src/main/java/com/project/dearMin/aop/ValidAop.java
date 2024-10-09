package com.project.dearMin.aop;

import com.project.dearMin.dto.account.request.AdminSignupReqDto;
import com.project.dearMin.exception.ValidException;
import com.project.dearMin.repository.AdminMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Aspect
public class ValidAop {
    @Autowired
    private AdminMapper adminMapper;

    @Pointcut("@annotation(com.project.dearMin.aop.annotation.ValidAspect)")
    private void pointCut() {}

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String methodName = proceedingJoinPoint.getSignature().getName();
        Object[] args = proceedingJoinPoint.getArgs();

        BeanPropertyBindingResult bindingResult = null;

        for (Object arg : args) {
            if (arg instanceof BeanPropertyBindingResult) {
                bindingResult = (BeanPropertyBindingResult) arg;
                break;
            }
        }

        if (bindingResult == null) {
            return proceedingJoinPoint.proceed();
        }

        // 아이디 중복체크
        if (methodName.equals("adminSignup")) {
            AdminSignupReqDto adminSignupReqDto = null;

            for (Object arg : args) {
                if (arg instanceof AdminSignupReqDto) {
                    adminSignupReqDto = (AdminSignupReqDto) arg;
                    break;
                }
            }

            if (adminSignupReqDto == null) {
                return proceedingJoinPoint.proceed();
            }

            if (adminMapper.findAdminByUsername(adminSignupReqDto.getUsername()) != null) {
                ObjectError objectError = new FieldError("username", "username", "이미 존재하는 아이디입니다");
                bindingResult.addError(objectError);
            }
        }

        // 발생한 에러 보내기
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError fieldError : fieldErrors) {
                String fieldName = fieldError.getField(); // dto 변수명
                String message = fieldError.getDefaultMessage(); // 메시지 내용
                errorMap.put(fieldName, message);
            }

            throw new ValidException(errorMap);
        }

        return proceedingJoinPoint.proceed();
    }
}