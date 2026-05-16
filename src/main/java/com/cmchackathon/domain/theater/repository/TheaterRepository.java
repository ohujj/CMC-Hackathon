package com.cmchackathon.domain.theater.repository;

import com.cmchackathon.domain.theater.entity.Theater;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TheaterRepository extends JpaRepository<Theater, String> {

    @Query("SELECT t FROM Theater t WHERE " +
           "(:keyword IS NULL OR t.theaName LIKE %:keyword% OR t.address LIKE %:keyword%)")
    Page<Theater> search(@Param("keyword") String keyword, Pageable pageable);
}
