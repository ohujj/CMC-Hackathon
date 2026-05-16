package com.cmchackathon.domain.user.controller;

import com.cmchackathon.domain.user.dto.UserMeResponse;
import com.cmchackathon.domain.user.dto.UserUpdateRequest;
import com.cmchackathon.domain.user.service.UserService;
import com.cmchackathon.global.response.ApiResponse;
import com.cmchackathon.domain.theater.dto.TheaterResponse;
import com.cmchackathon.domain.theater.service.TheaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "02. Me", description = "내 프로필 / 활동 / 저장한 영화관")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")
public class MeController {

    private final UserService userService;
    private final TheaterService theaterService;

    @Operation(summary = "내 프로필 + 활동요약")
    @GetMapping
    public ApiResponse<UserMeResponse> getMe(@AuthenticationPrincipal Long userId) {
        return ApiResponse.success(userService.getMe(userId));
    }

    @Operation(summary = "프로필 수정", description = "nickname / intro 부분 수정 (둘 다 옵셔널)")
    @PatchMapping
    public ApiResponse<UserMeResponse> updateMe(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return ApiResponse.success(userService.updateMe(userId, request));
    }

    @Operation(summary = "저장한 영화관 목록")
    @GetMapping("/theaters")
    public ApiResponse<Page<TheaterResponse>> getSavedTheaters(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 20, sort = "theaName", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ApiResponse.success(theaterService.getSavedTheaters(userId, pageable));
    }
}
