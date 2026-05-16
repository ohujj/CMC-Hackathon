package com.cmchackathon.domain.collection.controller;

import com.cmchackathon.domain.collection.service.CollectionService;
import com.cmchackathon.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Collection", description = "컬렉션 API")
@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @Operation(summary = "티켓 저장")
    @PostMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<Void>> addCollection(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long ticketId) {
        collectionService.addCollection(userId, ticketId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "티켓 저장 취소")
    @DeleteMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<Void>> removeCollection(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long ticketId) {
        collectionService.removeCollection(userId, ticketId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}