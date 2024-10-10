package com.project.dearMin.jwt;


import com.project.dearMin.entity.account.Admin;
import com.project.dearMin.repository.AdminMapper;
import com.project.dearMin.security.PrincipalUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Collections;
import java.util.Date;


@Slf4j
@Component
public class JwtProvider {
    private final Key key;
    private AdminMapper adminMapper;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Autowired AdminMapper adminMapper) {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.adminMapper = adminMapper;
    }

    public String generateToken(Admin admin) { // JWT 생성
        int adminId = admin.getAdminId();
        String username = admin.getUsername();
        Date expireDate = new Date(new Date().getTime() +( 1000 * 60 * 60 * 24 * 20));

        String accessToken = Jwts.builder()
                .claim("adminId", adminId)
                .claim("username", username)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return accessToken;
    }

    public String removeBearer(String token) { // Bearer 접두사 제거
        if(!StringUtils.hasText(token)) {
            return null;
        }
        return token.substring("Bearer ".length());
    }

    public Claims getClaims(String token) { // 토큰에서 클레임 추출
        Claims claims = null;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)// 토큰을 클래임으로 변환하는 작업
                    .getBody();
        } catch (Exception e) {
            log.error("JWT 인증 오류: {}", e.getMessage());
            return null;
        }

        return claims;
    }
    public Authentication getAuthentication(Claims claims) { // 사용자 인증
        String username = claims.get("username").toString();
        Admin admin = adminMapper.findAdminByUsername(username);
        if(admin == null) {
            return null;
        }

        PrincipalUser principalUser = admin.toPrincipalUser();
        return new UsernamePasswordAuthenticationToken(principalUser, principalUser.getPassword(), principalUser.getAuthorities());
    }

    public String generateAuthMailToken(String toMailAddress) { // 이메일
        Date exprireDate = new Date(new Date().getTime() + (1000 * 60 * 5));
        return Jwts.builder()
                .claim("toMailAddress",toMailAddress)
                .setExpiration(exprireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }



}
