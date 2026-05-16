package com.cmchackathon.domain.comment.entity;

import com.cmchackathon.domain.ticket.entity.Ticket;
import com.cmchackathon.domain.user.entity.User;
import com.cmchackathon.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column
    private LocalDateTime deletedAt;

    @Builder
    public Comment(User user, Ticket ticket, String content) {
        this.user = user;
        this.ticket = ticket;
        this.content = content;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
