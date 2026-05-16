package com.cmchackathon.domain.ticket.entity;

import com.cmchackathon.domain.ticket.dto.TicketUpdateRequest;
import com.cmchackathon.domain.user.entity.User;
import com.cmchackathon.global.entity.BaseEntity;
import com.cmchackathon.movie.entity.Movie;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ticket")
public class Ticket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_seq", nullable = false)
    private Movie movie;

    @Column(nullable = false)
    private LocalDate watchedDate;

    @Column(nullable = false)
    private LocalTime watchedTime;

    @Column(nullable = false)
    private String cinema;

    @Column(columnDefinition = "TEXT")
    private String review;

    @Column(nullable = false)
    private boolean showYn = false;

    @Column
    private LocalDateTime deletedAt;

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void update(TicketUpdateRequest request) {
        if (request.getWatchedDate() != null) this.watchedDate = request.getWatchedDate();
        if (request.getWatchedTime() != null) this.watchedTime = request.getWatchedTime();
        if (request.getCinema() != null) this.cinema = request.getCinema();
        if (request.getReview() != null) this.review = request.getReview();
    }

    @Builder
    public Ticket(User user, Long movieSeq, Movie movie, LocalDate watchedDate, LocalTime watchedTime, String cinema, String review) {
        this.user = user;
        this.movie = movie;
        this.watchedDate = watchedDate;
        this.watchedTime = watchedTime;
        this.cinema = cinema;
        this.review = review;
    }

    public void share() {
        this.showYn = true;
    }

    public void unshare() {
        this.showYn = false;
    }
}
