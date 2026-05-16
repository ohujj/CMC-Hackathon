package com.cmchackathon.domain.user.entity;

import com.cmchackathon.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(length = 200)
    private String intro;

    @Column
    private LocalDateTime deletedAt;

    @Builder
    public User(String loginId, String password, String nickname, String intro) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.intro = intro;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateIntro(String intro) {
        this.intro = intro;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
