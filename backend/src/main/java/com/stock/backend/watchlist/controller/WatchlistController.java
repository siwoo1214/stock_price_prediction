package com.stock.backend.watchlist.controller;

import com.stock.backend.common.response.ApiResponse;
import com.stock.backend.watchlist.dto.WatchlistAddRequest;
import com.stock.backend.watchlist.dto.WatchlistResponse;
import com.stock.backend.watchlist.service.WatchlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<List<WatchlistResponse>>>> getWatchlist(
            @AuthenticationPrincipal Long userId
    ) {
        return watchlistService.getWatchlist(userId)
                .map(list -> ResponseEntity.ok(ApiResponse.success(list)));
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<WatchlistResponse>>> addWatchlist(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody WatchlistAddRequest request
    ) {
        return watchlistService.addWatchlist(userId, request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response)));
    }

    @DeleteMapping("/{stockCode}")
    public Mono<ResponseEntity<ApiResponse<Void>>> removeWatchlist(
            @AuthenticationPrincipal Long userId,
            @PathVariable String stockCode
    ) {
        return watchlistService.removeWatchlist(userId, stockCode)
                .thenReturn(ResponseEntity.ok(ApiResponse.success(null)));
    }
}
