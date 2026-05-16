package com.cmchackathon.movie.dto;

import com.cmchackathon.movie.entity.Movie;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovieListResponse {
    private Long seq;
    private String korTitle;
    private String engTitle;
    private String director;
    private String productionYear;
    private String genreName;
    private String imagePath;

    public static MovieListResponse from(Movie movie) {
        return MovieListResponse.builder()
                .seq(movie.getSeq())
                .korTitle(movie.getKorTitle())
                .engTitle(movie.getEngTitle())
                .director(movie.getDirector())
                .productionYear(movie.getProductionYear())
                .genreName(movie.getGenreName())
                .imagePath(movie.getImagePath())
                .build();
    }
}
