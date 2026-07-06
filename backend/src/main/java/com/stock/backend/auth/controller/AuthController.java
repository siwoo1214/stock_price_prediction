package com.stock.backend.auth.controller;

import com.stock.backend.auth.dto.LoginRequest;
import com.stock.backend.auth.dto.LoginResponse;
import com.stock.backend.auth.dto.ReissueRequest;
import com.stock.backend.auth.dto.SignUpRequest;
import com.stock.backend.auth.dto.SignUpResponse;
import com.stock.backend.auth.dto.TokenReissueResponse;
import com.stock.backend.auth.service.AuthService;
import com.stock.backend.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public Mono<ResponseEntity<ApiResponse<SignUpResponse>>> signUp(@Valid @RequestBody SignUpRequest request) {
        return authService.signUp(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response)));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<ApiResponse<LoginResponse>>> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request)
                .map(response -> ResponseEntity.ok(ApiResponse.success(response)));
    }

    @PostMapping("/reissue")
    public Mono<ResponseEntity<ApiResponse<TokenReissueResponse>>> reissue(@Valid @RequestBody ReissueRequest request) {
        return authService.reissue(request)
                .map(response -> ResponseEntity.ok(ApiResponse.success(response)));
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<ApiResponse<Void>>> logout(@AuthenticationPrincipal Long userId) {
        return authService.logout(userId)
                .thenReturn(ResponseEntity.ok(ApiResponse.success(null)));
    }
}
