package com.cmchackathon.domain.collection.repository;

import com.cmchackathon.domain.collection.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    boolean existsByUserIdAndTicketIdAndDeletedAtIsNull(Long userId, Long ticketId);

    Optional<Collection> findByUserIdAndTicketIdAndDeletedAtIsNull(Long userId, Long ticketId);

    List<Collection> findByUserIdAndDeletedAtIsNull(Long userId);

    @Query("SELECT c FROM Collection c JOIN FETCH c.ticket t JOIN FETCH t.user JOIN FETCH t.movie WHERE c.user.id = :userId AND c.deletedAt IS NULL")
    List<Collection> findByUserIdWithDetails(@Param("userId") Long userId);
}
