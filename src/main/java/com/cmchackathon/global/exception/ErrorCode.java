package com.cmchackathon.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(400, 1001, "서버 오류가 발생했습니다."),
    NOT_FOUND(404, 1002, "요청한 리소스를 찾을 수 없습니다."),
    INVALID_INPUT(400, 1003, "잘못된 입력입니다.");

    private final int status;
    private final int code;
    private final String message;
}