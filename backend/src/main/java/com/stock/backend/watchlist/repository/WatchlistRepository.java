package com.stock.backend.watchlist.repository;

import com.stock.backend.watchlist.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    @Query("SELECT w FROM Watchlist w JOIN FETCH w.stock WHERE w.user.id = :userId")
    List<Watchlist> findAllWithStockByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndStockStockCode(Long userId, String stockCode);

    @Transactional
    long deleteByUserIdAndStockStockCode(Long userId, String stockCode);
}
