package com.cmchackathon.domain.user.controller;

import com.cmchackathon.domain.user.dto.LoginRequest;
import com.cmchackathon.domain.user.dto.LoginResponse;
import com.cmchackathon.domain.user.dto.NicknameResponse;
import com.cmchackathon.domain.user.dto.SignupRequest;
import com.cmchackathon.domain.user.service.UserService;
import com.cmchackathon.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "01. Auth", description = "회원가입 / 로그인 / 닉네임 생성")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.login(request)));
    }

    @Operation(summary = "회원가입", description = "loginId / password / nickname 으로 가입하고 JWT 반환")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<LoginResponse>> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.signup(request)));
    }

    @Operation(summary = "자동 닉네임 생성", description = "온보딩용 랜덤 닉네임 (DB 미저장)")
    @GetMapping("/nickname/random")
    public ApiResponse<NicknameResponse> randomNickname() {
        return ApiResponse.success(new NicknameResponse(userService.randomNickname()));
    }
}
