package com.cmchackathon.movie.repository;

import com.cmchackathon.movie.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m WHERE " +
           "(:keyword IS NULL OR m.korTitle LIKE %:keyword% OR m.engTitle LIKE %:keyword% OR m.director LIKE %:keyword%) AND " +
           "(:genre IS NULL OR m.genreName LIKE %:genre%) AND " +
           "(:year IS NULL OR m.productionYear = :year)")
    Page<Movie> search(
            @Param("keyword") String keyword,
            @Param("genre") String genre,
            @Param("year") String year,
            Pageable pageable
    );
}
