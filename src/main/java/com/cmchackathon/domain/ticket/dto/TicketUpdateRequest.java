package com.cmchackathon.domain.ticket.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class TicketUpdateRequest {

    private LocalDate watchedDate;

    private LocalTime watchedTime;

    private Integer rating;

    private String review;
}