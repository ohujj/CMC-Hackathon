package com.cmchackathon.domain.collection.repository;

import com.cmchackathon.domain.collection.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    boolean existsByUserIdAndTicketIdAndDeletedAtIsNull(Long userId, Long ticketId);

    Optional<Collection> findByUserIdAndTicketIdAndDeletedAtIsNull(Long userId, Long ticketId);
}
