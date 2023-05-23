package com.demo_security.token;

import com.demo_security.entity.User;
import com.demo_security.repository.UserInfoRepository;
import com.demo_security.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider jwtTokenProvider;

    @Autowired
    private UserInfoRepository repository;

    @Autowired
    private TokenService tokenService;

    public JwtAuthenticationFilter(TokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);
        String refreshToken = tokenService.getCachedValue();
        if (refreshToken != null) {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication authentica = jwtTokenProvider.getAuthentication(token);
                if (authentica != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentica);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
