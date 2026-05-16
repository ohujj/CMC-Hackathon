package com.cmchackathon.domain.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class TicketCreateRequest {

    @NotNull
    private Long movieSeq;

    @NotNull
    private LocalDate watchedDate;

    @NotNull
    private LocalTime watchedTime;

    @NotBlank
    private String cinema;

    private String review;
}
