package com.project.dearMin.security.filter;

import com.project.dearMin.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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

        Boolean isPermitAll = (Boolean) request.getAttribute("isPermitAll");

        if (isPermitAll == null || !isPermitAll) { // ! (not)을 걸어놨기 때문에 인증이 필요함
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
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 인증실패 (401 에러)
                    return;
                }

                Authentication authentication = jwtProvider.getAuthentication(claims);

                if (authentication == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 인증실패
                    return;
                }

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 인증실패
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
