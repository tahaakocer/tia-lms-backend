package com.tia.lms_backend.service;

import com.tia.lms_backend.dto.UserDto;
import com.tia.lms_backend.exception.GeneralUnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.NoOpResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class AuthService {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final EmployeeService employeeService;

    private final RedisTemplate<String, String> redisTemplate;
    public Map<String, Object> login(String username, String password) {
        log.info("Authenticating user: {}", username);

        String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&grant_type=password" +
                "&username=" + username +
                "&password=" + password;

        RestTemplate restTemplate = new RestTemplate();
        // DEFAULT ERROR HANDLER'I KAPAT:
        restTemplate.setErrorHandler(new NoOpResponseErrorHandler());

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("User authenticated successfully: {}", username);
            return response.getBody();
        } else if (response.getStatusCode().is4xxClientError()) {
            log.warn("Invalid credentials for user: {}", username);
            throw new GeneralUnauthorizedException("Invalid username or password.");
        } else {
            log.error("Unexpected error when authenticating user: {} | Status: {}", username, response.getStatusCode());
            throw new GeneralUnauthorizedException("Unexpected error during authentication.");
        }
    }


    public Map<String, Object> refreshToken(String refreshToken) {
        log.info("Refreshing token");

        String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&grant_type=refresh_token" +
                "&refresh_token=" + refreshToken;

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new NoOpResponseErrorHandler());

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Token refreshed successfully");
            return response.getBody();
        } else if (response.getStatusCode().is4xxClientError()) {
            log.warn("Invalid refresh token");
            throw new GeneralUnauthorizedException("Invalid refresh token.");
        } else {
            log.error("Unexpected error during token refresh: {}", response.getStatusCode());
            throw new GeneralUnauthorizedException("Unexpected error during token refresh.");
        }
    }

    public UserDto getUserInfo(Jwt jwt) {
        log.info("Fetching user info from JWT");

        String tckn = jwt.getClaim("preferred_username");
        String email = jwt.getClaim("email");

        return employeeService.getUserByTckn(tckn);
    }

    public void logout(Jwt jwt) {
        String jti = jwt.getId(); // JWT ID claim
        Long exp = jwt.getExpiresAt().toEpochMilli();
        Long now = System.currentTimeMillis();
        long ttl = (exp - now) / 1000; // saniye

        log.info("Blacklisting token jti={} for ttl={} sec", jti, ttl);
        redisTemplate.opsForValue().set("blacklist:" + jti, "true", Duration.ofSeconds(ttl));
    }
}
