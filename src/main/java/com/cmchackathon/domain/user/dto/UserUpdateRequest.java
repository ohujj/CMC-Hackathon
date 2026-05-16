package com.cmchackathon.domain.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {

    @Size(min = 2, max = 20)
    private String nickname;

    @Size(max = 200)
    private String intro;
}
