package com.cmchackathon.domain.like.repository;

import com.cmchackathon.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndTicketId(Long userId, Long ticketId);
    Optional<Like> findByUserIdAndTicketId(Long userId, Long ticketId);
    long countByTicketId(Long ticketId);
}