package com.cmchackathon.domain.theater.repository;

import com.cmchackathon.domain.theater.entity.SavedTheater;
import com.cmchackathon.domain.theater.entity.SavedTheaterId;
import com.cmchackathon.domain.theater.entity.Theater;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SavedTheaterRepository extends JpaRepository<SavedTheater, SavedTheaterId> {

    long countByIdUserId(Long userId);

    boolean existsByIdUserIdAndIdTheaCd(Long userId, String theaCd);

    void deleteByIdUserIdAndIdTheaCd(Long userId, String theaCd);

    @Query("SELECT t FROM Theater t WHERE t.theaCd IN " +
           "(SELECT s.id.theaCd FROM SavedTheater s WHERE s.id.userId = :userId)")
    Page<Theater> findSavedTheaters(@Param("userId") Long userId, Pageable pageable);
}
