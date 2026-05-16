package com.cmchackathon.theater.dto;

import com.cmchackathon.theater.entity.Theater;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TheaterResponse {
    private String theaCd;
    private String theaName;
    private String scrnName;
    private String screenGb;
    private String address;
    private String phone;
    private String homepage;
    private Integer seatCount;
    private String naverMapUrl;

    public static TheaterResponse from(Theater theater) {
        return TheaterResponse.builder()
                .theaCd(theater.getTheaCd())
                .theaName(theater.getTheaName())
                .scrnName(theater.getScrnName())
                .screenGb(theater.getScreenGb())
                .address(theater.getAddress())
                .phone(theater.getPhone())
                .homepage(theater.getHomepage())
                .seatCount(theater.getSeatCount())
                .naverMapUrl(theater.getNaverMapUrl())
                .build();
    }
}
