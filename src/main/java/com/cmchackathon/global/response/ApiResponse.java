package com.cmchackathon.global.response;

import com.cmchackathon.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {

    private final int code;
    private final T data;
    private final String message;

    public static ApiResponse<Void> fail(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getStatus(), null, errorCode.getMessage());
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(200, null, null);
    }
}