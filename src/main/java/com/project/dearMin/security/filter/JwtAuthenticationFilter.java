package com.project.dearMin.security.filter;

import com.project.dearMin.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
public class JwtAuthenticationFilter extends GenericFilter {

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 특정 경로는 인증 없이 통과
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/admin/auth/signup") || requestURI.startsWith("/admin/auth/signin")) {
            // 회원가입 및 로그인 요청은 필터링하지 않고 통과
            filterChain.doFilter(request, response);
            return;
        }

        Boolean isPermitAll = (Boolean) request.getAttribute("isPermitAll");

        // 인증이 필요한 경우만 필터 동작
        if (isPermitAll == null || !isPermitAll) {
            String accessToken = request.getHeader("Authorization");

            if (accessToken != null && accessToken.startsWith("Bearer ")) {
                String removedBearerToken = jwtProvider.removeBearer(accessToken);
                Claims claims;

                try {
                    claims = jwtProvider.getClaims(removedBearerToken);
                    if (claims == null) {
                        throw new Exception("Invalid JWT token");
                    }
                } catch (Exception e) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 인증 실패
                    return;
                }

                Authentication authentication = jwtProvider.getAuthentication(claims);

                if (authentication == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 인증 실패
                    return;
                }

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 인증 실패
                return;
            }
        }

        // 나머지 요청은 필터링 후 처리
        filterChain.doFilter(request, response);
    }
}
