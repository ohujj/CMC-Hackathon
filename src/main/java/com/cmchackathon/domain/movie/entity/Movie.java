package com.cmchackathon.domain.movie.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movie")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Movie {

    @Id
    @Column(name = "seq")
    private Long seq; // indieground 고유 ID (PK 직접 사용)

    @Column(name = "kor_title", length = 200)
    private String korTitle;

    @Column(name = "eng_title", length = 200)
    private String engTitle;

    @Column(name = "director", length = 200)
    private String director;

    @Column(name = "actors", columnDefinition = "TEXT")
    private String actors;

    @Column(name = "production_year", length = 10)
    private String productionYear;

    @Column(name = "genre_code", length = 20)
    private String genreCode;

    @Column(name = "genre_name", length = 50)
    private String genreName;

    @Column(name = "company_nm", length = 200)
    private String companyNm;        // 제작사

    @Column(name = "distributor_nm", length = 200)
    private String distributorNm;   // 배급사

    @Column(name = "image_path", length = 300)
    private String imagePath;       // fileFolder/xxx_jpg

    @Column(name = "lss_gubun", length = 10)
    private String lssGubun;

    // 상세 페이지 추가 필드
    @Column(name = "duration", length = 30)
    private String duration;        // 83분 54초

    @Column(name = "rating", length = 30)
    private String rating;          // 15세이상 관람가

    @Column(name = "color_type", length = 20)
    private String colorType;       // 컬러 / 흑백

    @Column(name = "synopsis", columnDefinition = "TEXT")
    private String synopsis;

    @Column(name = "screenwriter", length = 200)
    private String screenwriter;

    @Column(name = "producer", length = 300)
    private String producer;

    @Column(name = "release_date", length = 30)
    private String releaseDate;     // 2026년 06월 04일

    @Column(name = "keywords", length = 300)
    private String keywords;        // #드라마 #로맨스

    @Column(name = "detail_crawled")
    private boolean detailCrawled = false;
}
