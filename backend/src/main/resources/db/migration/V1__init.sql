CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE stocks (
    stock_code VARCHAR(20) PRIMARY KEY,
    company_name VARCHAR(100) NOT NULL,
    market_type VARCHAR(20),
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE watchlist (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    stock_code VARCHAR(20) NOT NULL REFERENCES stocks(stock_code),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_watchlist_user_stock UNIQUE (user_id, stock_code)
);
