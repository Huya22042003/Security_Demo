package com.demo_security.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TokenProvider {

    private static final String SECRET_KEY = "yourSecretKey";
    private static final long ACCESS_TOKEN_EXPIRATION_TIME_MS = 15 * 60 * 1000;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME_MS = 7 * 24 * 60 * 60 * 1000;

    public String generateToken(Authentication authentication) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME_MS);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    @Cacheable(value = RefreshTokenContants.REFRESH_TOKEN, key = "T(java.util.Objects).hash(#authentication.getName())")
    public String generateRefreshToken(Authentication authentication) {
        String keyName = "bbbb";

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME_MS);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();

            // Kiểm tra thời gian hết hạn
            Date expirationDate = claims.getExpiration();
            Date currentDate = new Date();
            if (currentDate.after(expirationDate)) {
                return false;
            }

            return true;
        } catch (Exception e) {
            // Xử lý exception khi token không hợp lệ
            return false;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.replaceAll("Bearer ", ""); // Lấy phần token sau "Bearer "
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();
        // Trích xuất các thông tin khác từ claims (ví dụ: roles, permissions, ...)

        List<GrantedAuthority> authorities = new ArrayList<>();
        // Tạo đối tượng GrantedAuthority dựa trên vai trò và quyền hạn

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
}
