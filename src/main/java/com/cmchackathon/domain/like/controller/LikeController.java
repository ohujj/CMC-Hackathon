package com.cmchackathon.domain.like.controller;

import com.cmchackathon.domain.like.service.LikeService;
import com.cmchackathon.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Like", description = "좋아요 API")
@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "좋아요 추가")
    @PostMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<Void>> addLike(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long ticketId) {
        likeService.addLike(userId, ticketId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "좋아요 취소")
    @DeleteMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<Void>> removeLike(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long ticketId) {
        likeService.removeLike(userId, ticketId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}