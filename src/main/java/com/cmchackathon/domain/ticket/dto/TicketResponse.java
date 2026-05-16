package com.cmchackathon.domain.ticket.dto;

import com.cmchackathon.domain.ticket.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class TicketResponse {

    private Long id;
    private Long movieSeq;
    private LocalDate watchedDate;
    private LocalTime watchedTime;
    private String cinema;
    private String review;
    private boolean showYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getMovie().getSeq(),
                ticket.getWatchedDate(),
                ticket.getWatchedTime(),
                ticket.getCinema(),
                ticket.getReview(),
                ticket.isShowYn(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
}