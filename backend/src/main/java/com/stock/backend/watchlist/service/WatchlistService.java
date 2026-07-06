package com.stock.backend.watchlist.service;

import com.stock.backend.auth.entity.User;
import com.stock.backend.auth.repository.UserRepository;
import com.stock.backend.common.exception.BusinessException;
import com.stock.backend.common.exception.ErrorCode;
import com.stock.backend.stock.entity.Stock;
import com.stock.backend.stock.repository.StockRepository;
import com.stock.backend.watchlist.dto.WatchlistAddRequest;
import com.stock.backend.watchlist.dto.WatchlistResponse;
import com.stock.backend.watchlist.entity.Watchlist;
import com.stock.backend.watchlist.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    public Mono<List<WatchlistResponse>> getWatchlist(Long userId) {
        return Mono.fromCallable(() -> watchlistRepository.findAllWithStockByUserId(userId))
                .subscribeOn(Schedulers.boundedElastic())
                .map(watchlists -> watchlists.stream()
                        .map(WatchlistResponse::from)
                        .toList());
    }

    public Mono<WatchlistResponse> addWatchlist(Long userId, WatchlistAddRequest request) {
        return Mono.fromCallable(() -> registerWatchlist(userId, request))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> removeWatchlist(Long userId, String stockCode) {
        return Mono.fromRunnable(() -> deleteWatchlist(userId, stockCode))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private WatchlistResponse registerWatchlist(Long userId, WatchlistAddRequest request) {
        Stock stock = stockRepository.findById(request.stockCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND));

        if (watchlistRepository.existsByUserIdAndStockStockCode(userId, request.stockCode())) {
            throw new BusinessException(ErrorCode.DUPLICATE_WATCHLIST);
        }

        User userRef = userRepository.getReferenceById(userId);

        Watchlist watchlist = Watchlist.builder()
                .user(userRef)
                .stock(stock)
                .build();

        try {
            Watchlist saved = watchlistRepository.save(watchlist);
            return WatchlistResponse.from(saved);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.DUPLICATE_WATCHLIST);
        }
    }

    private void deleteWatchlist(Long userId, String stockCode) {
        long deleted = watchlistRepository.deleteByUserIdAndStockStockCode(userId, stockCode);
        if (deleted == 0) {
            throw new BusinessException(ErrorCode.WATCHLIST_NOT_FOUND);
        }
    }
}
