 package com.cmchackathon.domain.user.controller;

import com.cmchackathon.domain.user.dto.UserMeResponse;
import com.cmchackathon.domain.user.service.UserService;
import com.cmchackathon.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "01. Auth", description = "회원가입 / 로그인 / 닉네임 생성 / 내 프로필")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")
public class MeController {

    private final UserService userService;

    @Operation(summary = "내 프로필 조회")
    @GetMapping
    public ApiResponse<UserMeResponse> getMe(@AuthenticationPrincipal Long userId) {
        return ApiResponse.success(userService.getMe(userId));
    }
}
