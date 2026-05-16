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
    private long ticketCount;        // 작성한 티켓 수 (티켓 도메인 완성 전 0)
    private long savedTicketCount;   // 저장한 티켓 수 (티켓 도메인 완성 전 0)
    private long savedTheaterCount;  // 저장한 영화관 수

    public static UserMeResponse of(User user, long savedTheaterCount) {
        return UserMeResponse.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .intro(user.getIntro())
                .ticketCount(0)
                .savedTicketCount(0)
                .savedTheaterCount(savedTheaterCount)
                .build();
    }
}
