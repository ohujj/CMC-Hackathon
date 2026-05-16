package com.cmchackathon.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "로그인 요청")
public class LoginRequest {

    @NotBlank
    @Schema(description = "로그인 아이디", example = "user1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String loginId;

    @NotBlank
    @Schema(description = "비밀번호", example = "password1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
