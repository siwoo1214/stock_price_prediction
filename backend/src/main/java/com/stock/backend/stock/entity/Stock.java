package com.stock.backend.stock.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "stocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @Column(name = "stock_code", length = 20)
    private String stockCode;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "market_type", length = 20)
    private String marketType;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private Stock(String stockCode, String companyName, String marketType) {
        this.stockCode = stockCode;
        this.companyName = companyName;
        this.marketType = marketType;
    }

    /**
     * 객체를 만들 때 계속해서 localDateTime 이런거 안쓰게 하고
     * 저장 직전이나 수정 직전에 자동으로 채워지게끔 만들려고
     * @PrePersist 를 붙였음
     */
    @PrePersist
    @PreUpdate
    protected void onSave() {
        this.updatedAt = LocalDateTime.now();
    }
}
