package com.cmchackathon.domain.ticket.repository;

import com.cmchackathon.domain.ticket.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByUserIdAndDeletedAtIsNull(Long userId, Pageable pageable);

    List<Ticket> findByUserIdAndDeletedAtIsNull(Long userId);

    Optional<Ticket> findByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT t FROM Ticket t JOIN FETCH t.user JOIN FETCH t.movie WHERE t.showYn = true AND t.deletedAt IS NULL ORDER BY t.createdAt DESC")
    List<Ticket> findAllPublicTicketsOrderByCreatedAt();

    @Query("SELECT t FROM Ticket t JOIN FETCH t.user JOIN FETCH t.movie LEFT JOIN Like l ON l.ticket = t WHERE t.showYn = true AND t.deletedAt IS NULL GROUP BY t ORDER BY COUNT(l) DESC")
    List<Ticket> findAllPublicTicketsOrderByLikeCount();
}