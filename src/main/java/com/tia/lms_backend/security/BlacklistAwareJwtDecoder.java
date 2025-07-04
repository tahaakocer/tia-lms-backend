package com.tia.lms_backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

@RequiredArgsConstructor
public class BlacklistAwareJwtDecoder implements JwtDecoder {

    private final JwtDecoder delegate;
    private final StringRedisTemplate redisTemplate;

    @Override
    public Jwt decode(String token) throws JwtException {
        Jwt jwt = delegate.decode(token);

        String jti = jwt.getId();
        if (jti != null && Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + jti))) {
            throw new JwtException("Token is blacklisted");
        }

        return jwt;
    }
}
