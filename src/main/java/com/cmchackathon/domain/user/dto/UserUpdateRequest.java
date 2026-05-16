package com.cmchackathon.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "프로필 수정 요청 (nickname / intro 부분 수정, 둘 다 옵셔널)")
public class UserUpdateRequest {

    @Size(min = 2, max = 20)
    @Schema(description = "변경할 닉네임 (선택)", example = "새닉네임", nullable = true)
    private String nickname;

    @Size(max = 200)
    @Schema(description = "변경할 자기소개 (선택, 최대 200자)", example = "독립영화 보는 게 취미입니다.", nullable = true)
    private String intro;
}
