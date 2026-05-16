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
    private Integer rating;
    private String review;
    private boolean showYn;
    private boolean isLiked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long likeCount;

    public static TicketResponse from(Ticket ticket, boolean isLiked, long likeCount) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getMovie().getSeq(),
                ticket.getWatchedDate(),
                ticket.getWatchedTime(),
                ticket.getRating(),
                ticket.getReview(),
                ticket.isShowYn(),
                isLiked,
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                likeCount
        );
    }

}