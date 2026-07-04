package com.stock.backend.auth.service;

import com.stock.backend.auth.dto.SignUpRequest;
import com.stock.backend.auth.dto.SignUpResponse;
import com.stock.backend.auth.entity.User;
import com.stock.backend.auth.repository.UserRepository;
import com.stock.backend.common.exception.BusinessException;
import com.stock.backend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}
