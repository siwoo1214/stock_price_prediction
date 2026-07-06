package com.stock.backend.auth.dto;

public record TokenReissueResponse(
        String accessToken,
        long accessTokenExpiresIn
) {
}
