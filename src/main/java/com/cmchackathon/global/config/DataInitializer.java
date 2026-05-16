package com.cmchackathon.global.config;

import com.cmchackathon.domain.user.entity.User;
import com.cmchackathon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            for (int i = 1; i <= 10; i++) {
                userRepository.save(User.builder()
                        .loginId("user" + i)
                        .password(passwordEncoder.encode("password" + i))
                        .nickname("유저" + i)
                        .build());
            }
        }
    }
}