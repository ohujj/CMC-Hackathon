package com.cmchackathon.domain.ticket.dto;

import com.cmchackathon.domain.comment.dto.CommentResponse;
import com.cmchackathon.domain.ticket.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class TicketDetailResponse {

    private Long id;
    private Long movieSeq;
    private LocalDate watchedDate;
    private LocalTime watchedTime;
    private String cinema;
    private String review;
    private boolean showYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long likeCount;
    private boolean isLiked;
    private boolean isCollected;
    private List<CommentResponse> comments;

    public static TicketDetailResponse of(Ticket ticket, long likeCount, boolean isLiked, boolean isCollected, List<CommentResponse> comments) {
        return new TicketDetailResponse(
                ticket.getId(),
                ticket.getMovie().getSeq(),
                ticket.getWatchedDate(),
                ticket.getWatchedTime(),
                ticket.getCinema(),
                ticket.getReview(),
                ticket.isShowYn(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                likeCount,
                isLiked,
                isCollected,
                comments
        );
    }
}
