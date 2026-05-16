package com.cmchackathon.domain.user.dto;

import com.cmchackathon.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserMeResponse {
    private Long id;
    private String loginId;
    private String nickname;
    private String intro;

    public static UserMeResponse of(User user) {
        return UserMeResponse.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .intro(user.getIntro())
                .build();
    }
}
