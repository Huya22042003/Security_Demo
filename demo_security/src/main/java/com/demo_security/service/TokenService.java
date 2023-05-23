package com.demo_security.service;

import com.demo_security.entity.ResponseToken;
import com.demo_security.token.RefreshTokenContants;
import com.demo_security.token.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CacheManager cacheManager;

    public ResponseToken generateTokens(Authentication authentication) {
        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        return new ResponseToken(accessToken, refreshToken);
    }

    public String getCachedValue() {
        Cache cache = cacheManager.getCache("aaaaa");
        if (cache != null) {
            Cache.ValueWrapper wrapper = cache.get("aaaaa");
            if (wrapper != null) {
                return (String) wrapper.get();
            }
        }
        return null; // Giá trị không tồn tại trong cache
    }

    public void invalidateToken() {
        // Xóa refreshToken khỏi danh sách token hoạt động
        cacheManager.getCache("aaaaa").clear();
    }

}
