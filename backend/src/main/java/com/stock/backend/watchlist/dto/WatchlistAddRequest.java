package com.stock.backend.watchlist.dto;

import jakarta.validation.constraints.NotBlank;

public record WatchlistAddRequest(

        @NotBlank(message = "종목 코드는 필수입니다.")
        String stockCode
) {
}
