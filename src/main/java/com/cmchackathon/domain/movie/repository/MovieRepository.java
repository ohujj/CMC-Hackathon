package com.cmchackathon.domain.movie.repository;

import com.cmchackathon.domain.movie.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m WHERE " +
           ":keyword IS NULL OR m.korTitle LIKE %:keyword% OR m.engTitle LIKE %:keyword% OR m.director LIKE %:keyword%")
    Page<Movie> search(@Param("keyword") String keyword, Pageable pageable);
}
