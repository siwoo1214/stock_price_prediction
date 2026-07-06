package com.stock.backend.auth.service;

import com.stock.backend.auth.dto.LoginRequest;
import com.stock.backend.auth.dto.LoginResponse;
import com.stock.backend.auth.dto.ReissueRequest;
import com.stock.backend.auth.dto.SignUpRequest;
import com.stock.backend.auth.dto.SignUpResponse;
import com.stock.backend.auth.dto.TokenReissueResponse;
import com.stock.backend.auth.entity.User;
import com.stock.backend.auth.repository.UserRepository;
import com.stock.backend.common.exception.BusinessException;
import com.stock.backend.common.exception.ErrorCode;
import com.stock.backend.common.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ReactiveStringRedisTemplate redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    public Mono<SignUpResponse> signUp(SignUpRequest request) {
        return Mono.fromCallable(() -> registerUser(request))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private SignUpResponse registerUser(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        try {
            User saved = userRepository.save(user);
            return SignUpResponse.from(saved);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    public Mono<LoginResponse> login(LoginRequest request) {
        return Mono.fromCallable(() -> authenticate(request))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(this::issueTokens);
    }

    private User authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        return user;
    }

    private Mono<LoginResponse> issueTokens(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        String redisKey = REFRESH_TOKEN_PREFIX + user.getId();
        Duration ttl = Duration.ofMillis(jwtTokenProvider.getRefreshTokenExpiration());

        return redisTemplate.opsForValue()
                .set(redisKey, refreshToken, ttl)
                .thenReturn(new LoginResponse(
                        accessToken,
                        refreshToken,
                        jwtTokenProvider.getAccessTokenExpiration()
                ));
    }

    public Mono<TokenReissueResponse> reissue(ReissueRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtTokenProvider.isValid(refreshToken)) {
            return Mono.error(new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);
        String redisKey = REFRESH_TOKEN_PREFIX + userId;

        return redisTemplate.opsForValue().get(redisKey)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN)))
                .flatMap(storedToken -> {
                    if (!storedToken.equals(refreshToken)) {
                        return Mono.error(new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));
                    }

                    String newAccessToken = jwtTokenProvider.createAccessToken(userId, null);
                    return Mono.just(new TokenReissueResponse(
                            newAccessToken,
                            jwtTokenProvider.getAccessTokenExpiration()
                    ));
                });
    }

    public Mono<Void> logout(Long userId) {
        String redisKey = REFRESH_TOKEN_PREFIX + userId;
        return redisTemplate.delete(redisKey).then();
    }
}
