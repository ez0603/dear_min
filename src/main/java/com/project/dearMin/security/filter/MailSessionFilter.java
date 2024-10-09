package com.project.dearMin.security.filter;


import com.project.dearMin.exception.RequestInLimitTimeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

@Component
public class MailSessionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession();
        long expireTime = (1000 * 60 * 3);
        long nowTime = new Date().getTime();
        Date lastReqDate = (Date) session.getAttribute("timer");
        long lastReqDateTime = 0;
        if (lastReqDate != null) {
            lastReqDateTime = lastReqDate.getTime();
            long timeInterval = Math.abs(nowTime - lastReqDateTime);
            if (timeInterval < expireTime) {

                long totalSeconds = (expireTime - timeInterval) / 1000;
                long minutes = totalSeconds / 60;
                long seconds = totalSeconds % 60;
                System.out.println(totalSeconds);

                try {
                    throw new RequestInLimitTimeException(totalSeconds);
                } catch (RequestInLimitTimeException e) {
                    response.sendError(HttpStatus.BAD_REQUEST.value(), "잔여시간: " + minutes + ":" + seconds);
                }

            }
        }

        filterChain.doFilter(request, response);

    }

}