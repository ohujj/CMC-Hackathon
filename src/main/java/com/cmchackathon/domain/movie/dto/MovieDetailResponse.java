package com.cmchackathon.domain.movie.dto;

import com.cmchackathon.domain.movie.entity.Movie;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovieDetailResponse {
    private Long seq;
    private String korTitle;
    private String engTitle;
    private String director;
    private String actors;
    private String productionYear;
    private String genreName;
    private String companyNm;
    private String distributorNm;
    private String imagePath;
    private String duration;
    private String rating;
    private String colorType;
    private String synopsis;
    private String screenwriter;
    private String producer;
    private String releaseDate;
    private String keywords;

    public static MovieDetailResponse from(Movie movie) {
        return MovieDetailResponse.builder()
                .seq(movie.getSeq())
                .korTitle(movie.getKorTitle())
                .engTitle(movie.getEngTitle())
                .director(movie.getDirector())
                .actors(movie.getActors())
                .productionYear(movie.getProductionYear())
                .genreName(movie.getGenreName())
                .companyNm(movie.getCompanyNm())
                .distributorNm(movie.getDistributorNm())
                .imagePath(stripFolder(movie.getImagePath()))
                .duration(stripSeconds(movie.getDuration()))
                .rating(movie.getRating())
                .colorType(movie.getColorType())
                .synopsis(movie.getSynopsis())
                .screenwriter(movie.getScreenwriter())
                .producer(movie.getProducer())
                .releaseDate(movie.getReleaseDate())
                .keywords(movie.getKeywords())
                .build();
    }

    private static String stripSeconds(String duration) {
        if (duration == null) return null;
        int idx = duration.indexOf('분');
        return idx >= 0 ? duration.substring(0, idx + 1) : duration;
    }

    private static String stripFolder(String path) {
        if (path == null) return null;
        return path.startsWith("fileFolder/") ? path.substring("fileFolder/".length()) : path;
    }
}
