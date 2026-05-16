package com.cmchackathon.domain.theater.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_theater")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavedTheater {

    @EmbeddedId
    private SavedTheaterId id;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public SavedTheater(Long userId, String theaCd) {
        this.id = new SavedTheaterId(userId, theaCd);
    }
}
