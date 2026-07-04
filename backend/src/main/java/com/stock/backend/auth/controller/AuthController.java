package com.stock.backend.auth.controller;

import com.stock.backend.auth.dto.SignUpRequest;
import com.stock.backend.auth.dto.SignUpResponse;
import com.stock.backend.auth.service.AuthService;
import com.stock.backend.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
