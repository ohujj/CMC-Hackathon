package com.cmchackathon.domain.like.repository;

import com.cmchackathon.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndTicketId(Long userId, Long ticketId);
    Optional<Like> findByUserIdAndTicketId(Long userId, Long ticketId);
    long countByTicketId(Long ticketId);

    @Query("SELECT l.ticket.id FROM Like l WHERE l.user.id = :userId")
    Set<Long> findTicketIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT l.ticket.id, COUNT(l) FROM Like l WHERE l.ticket.id IN :ticketIds GROUP BY l.ticket.id")
    List<Object[]> countByTicketIds(@Param("ticketIds") List<Long> ticketIds);
}