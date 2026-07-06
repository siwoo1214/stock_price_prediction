package com.stock.backend.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        long accessTokenExpiresIn
) {
}
