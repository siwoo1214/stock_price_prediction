package com.stock.backend.auth.dto;

import com.stock.backend.auth.entity.User;

public record SignUpResponse(Long userId, String email) {

    public static SignUpResponse from(User user) {
        return new SignUpResponse(user.getId(), user.getEmail());
    }
}
