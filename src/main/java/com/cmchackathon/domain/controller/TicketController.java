package com.cmchackathon.domain.controller;

import com.cmchackathon.domain.ticket.dto.TicketCreateRequest;
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

@Tag(name = "Ticket", description = "티켓 API")
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
}