package com.tia.lms_backend.controller;

import com.tia.lms_backend.dto.UserDto;
import com.tia.lms_backend.dto.response.GeneralResponse;
import com.tia.lms_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> login(
            @RequestParam String username,
            @RequestParam String password
    ) {
        Map<String, Object> tokenResponse = authService.login(username, password);
        return ResponseEntity.ok(GeneralResponse.<Map<String, Object>>builder()
                .code(200)
                .message("Login successful.")
                .data(tokenResponse)
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<GeneralResponse<Map<String, Object>>> refreshToken(
            @RequestParam String refreshToken
    ) {
        Map<String, Object> tokenResponse = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(GeneralResponse.<Map<String, Object>>builder()
                .code(200)
                .message("Token refreshed successfully.")
                .data(tokenResponse)
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<GeneralResponse<UserDto>> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        UserDto userInfo = authService.getUserInfo(jwt);
        return ResponseEntity.ok(GeneralResponse.<UserDto>builder()
                .code(200)
                .message("User info retrieved successfully.")
                .data(userInfo)
                .build());
    }
    @PostMapping("/logout")
    public ResponseEntity<GeneralResponse<Void>> logout(@AuthenticationPrincipal Jwt jwt) {
        authService.logout(jwt);
        return ResponseEntity.ok(GeneralResponse.<Void>builder()
                .code(200)
                .message("Logged out successfully.")
                .build());
    }



}
