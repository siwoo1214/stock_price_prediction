package com.stock.backend.watchlist.entity;

import com.stock.backend.auth.entity.User;
import com.stock.backend.stock.entity.Stock;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "watchlist",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "stock_code"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_code", nullable = false)
    private Stock stock;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private Watchlist(User user, Stock stock) {
        this.user = user;
        this.stock = stock;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
