package com.stock.backend.watchlist.dto;

import com.stock.backend.watchlist.entity.Watchlist;

public record WatchlistResponse(
        Long watchlistId,
        String stockCode,
        String companyName,
        String marketType
) {
    public static WatchlistResponse from(Watchlist watchlist) {
        return new WatchlistResponse(
                watchlist.getId(),
                watchlist.getStock().getStockCode(),
                watchlist.getStock().getCompanyName(),
                watchlist.getStock().getMarketType()
        );
    }
}
