package com.cmchackathon.domain.collection.dto;

import com.cmchackathon.domain.collection.entity.Collection;
import com.cmchackathon.domain.ticket.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class CollectionResponse {

    private Long ticketId;
    private Long movieSeq;
    private LocalDate watchedDate;
    private LocalTime watchedTime;
    private String cinema;
    private String review;
    private boolean showYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String ownerNickname;

    public static CollectionResponse from(Collection collection) {
        Ticket ticket = collection.getTicket();
        return new CollectionResponse(
                ticket.getId(),
                ticket.getMovie().getSeq(),
                ticket.getWatchedDate(),
                ticket.getWatchedTime(),
                ticket.getCinema(),
                ticket.getReview(),
                ticket.isShowYn(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                ticket.getUser().getNickname()
        );
    }
}