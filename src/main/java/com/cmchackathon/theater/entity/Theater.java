package com.cmchackathon.theater.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "theater")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Theater {

    @Id
    @Column(name = "thea_cd", length = 20)
    private String theaCd;

    @Column(name = "scrn_cd", length = 10, nullable = false)
    private String scrnCd;

    @Column(name = "thea_name", length = 200)
    private String theaName;

    @Column(name = "scrn_name", length = 200)
    private String scrnName;

    @Column(name = "screen_gb", length = 10)
    private String screenGb;

    @Column(name = "designated_at", length = 30)
    private String designatedAt;

    @Column(name = "address", length = 300)
    private String address;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "fax", length = 50)
    private String fax;

    @Column(name = "homepage", length = 300)
    private String homepage;

    @Column(name = "seat_count")
    private Integer seatCount;

    @Column(name = "naver_map_url", length = 500)
    private String naverMapUrl;

    @Builder.Default
    @Column(name = "detail_crawled")
    private boolean detailCrawled = false;
}
