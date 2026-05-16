package com.cmchackathon.domain.user.service;

import com.cmchackathon.domain.user.dto.LoginRequest;
import com.cmchackathon.domain.user.dto.LoginResponse;
import com.cmchackathon.domain.user.dto.SignupRequest;
import com.cmchackathon.domain.user.dto.UserMeResponse;
import com.cmchackathon.domain.user.dto.UserUpdateRequest;
import com.cmchackathon.domain.user.entity.User;
import com.cmchackathon.domain.user.repository.UserRepository;
import com.cmchackathon.global.exception.BusinessException;
import com.cmchackathon.global.exception.ErrorCode;
import com.cmchackathon.global.jwt.JwtProvider;
import com.cmchackathon.domain.theater.repository.SavedTheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final NicknameGenerator nicknameGenerator;
    private final SavedTheaterRepository savedTheaterRepository;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        String token = jwtProvider.generateToken(user.getId());
        return new LoginResponse(token);
    }

    public LoginResponse signup(SignupRequest request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }
        User user = User.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();
        userRepository.save(user);
        return new LoginResponse(jwtProvider.generateToken(user.getId()));
    }

    @Transactional(readOnly = true)
    public String randomNickname() {
        for (int i = 0; i < 10; i++) {
            String candidate = nicknameGenerator.generate();
            if (!userRepository.existsByNickname(candidate)) {
                return candidate;
            }
        }
        return nicknameGenerator.generate();
    }

    @Transactional(readOnly = true)
    public UserMeResponse getMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        long savedTheaters = savedTheaterRepository.countByIdUserId(userId);
        return UserMeResponse.of(user, savedTheaters);
    }

    public UserMeResponse updateMe(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
            if (userRepository.existsByNickname(request.getNickname())) {
                throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
            }
            user.updateNickname(request.getNickname());
        }
        if (request.getIntro() != null) {
            user.updateIntro(request.getIntro());
        }

        long savedTheaters = savedTheaterRepository.countByIdUserId(userId);
        return UserMeResponse.of(user, savedTheaters);
    }
}
