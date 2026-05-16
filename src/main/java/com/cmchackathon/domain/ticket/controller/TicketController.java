package com.cmchackathon.domain.ticket.controller;

import com.cmchackathon.domain.ticket.dto.TicketCreateRequest;
import com.cmchackathon.domain.ticket.dto.TicketDetailResponse;
import com.cmchackathon.domain.ticket.dto.TicketResponse;
import com.cmchackathon.domain.ticket.dto.TicketUpdateRequest;
import com.cmchackathon.domain.ticket.service.TicketService;
import com.cmchackathon.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "04. Ticket", description = "관람한 영화 티켓 작성 / 수정")
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @Operation(summary = "티켓 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createTicket(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody TicketCreateRequest request) {
        ticketService.createTicket(userId, request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "티켓 수정")
    @PatchMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<Void>> updateTicket(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long ticketId,
            @RequestBody TicketUpdateRequest request) {
        ticketService.updateTicket(userId, ticketId, request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "티켓 삭제")
    @DeleteMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<Void>> deleteTicket(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long ticketId) {
        ticketService.deleteTicket(userId, ticketId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "내 티켓 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getMyTickets(
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getMyTickets(userId)));
    }

    @Operation(summary = "티켓 상세 조회")
    @GetMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<TicketDetailResponse>> getTicketDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long ticketId) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getTicketDetail(userId, ticketId)));
    }

    @Operation(summary = "공개 티켓 목록 조회")
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getPublicTickets(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "latest") String sort) {
        return ResponseEntity.ok(ApiResponse.success(ticketService.getPublicTickets(userId, sort)));
    }

    @Operation(summary = "티켓 공개 여부 변경")
    @PatchMapping("/{ticketId}/share")
    public ResponseEntity<ApiResponse<Void>> updateShowYn(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long ticketId,
            @RequestParam boolean showYn) {
        ticketService.updateShowYn(userId, ticketId, showYn);
        return ResponseEntity.ok(ApiResponse.success());
    }
}