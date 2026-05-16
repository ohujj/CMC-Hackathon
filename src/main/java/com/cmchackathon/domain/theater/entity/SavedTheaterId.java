package com.cmchackathon.domain.theater.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SavedTheaterId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "thea_cd", length = 20)
    private String theaCd;
}
