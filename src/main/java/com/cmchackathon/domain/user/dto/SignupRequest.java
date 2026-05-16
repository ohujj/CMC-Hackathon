package com.cmchackathon.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "회원가입 요청")
public class SignupRequest {

    @NotBlank
    @Size(min = 4, max = 30)
    @Schema(description = "로그인 아이디 (4~30자)", example = "user1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String loginId;

    @NotBlank
    @Size(min = 4, max = 60)
    @Schema(description = "비밀번호 (4~60자)", example = "password1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotBlank
    @Size(min = 2, max = 20)
    @Schema(description = "닉네임 (2~20자, 중복불가)", example = "독립영화매니아", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickname;
}
