package com.stock.backend.common.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationWebFilter implements WebFilter {

    private static final String HEADER_NAME = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String header = exchange.getRequest().getHeaders().getFirst(HEADER_NAME);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            return chain.filter(exchange);
        }

        String token = header.substring(TOKEN_PREFIX.length());

        if (!jwtTokenProvider.isValid(token)) {
            return chain.filter(exchange);
        }

        Long userId = jwtTokenProvider.getUserId(token);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }
}
