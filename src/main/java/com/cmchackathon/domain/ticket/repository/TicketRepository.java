package com.cmchackathon.domain.ticket.repository;

import com.cmchackathon.domain.ticket.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByUserIdAndDeletedAtIsNull(Long userId, Pageable pageable);

    List<Ticket> findByUserIdAndDeletedAtIsNull(Long userId);

    Optional<Ticket> findByIdAndDeletedAtIsNull(Long id);
}