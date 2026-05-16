package com.cmchackathon.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(500, 1001, "서버 오류가 발생했습니다."),
    NOT_FOUND(404, 1002, "요청한 리소스를 찾을 수 없습니다."),
    INVALID_INPUT(400, 1003, "잘못된 입력입니다."),
    MISSING_PARAMETER(400, 1004, "필수 파라미터가 누락되었습니다."),
    TYPE_MISMATCH(400, 1005, "파라미터 타입이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(405, 1006, "허용되지 않은 HTTP 메서드입니다."),
    UNAUTHORIZED(401, 1007, "인증이 필요합니다."),
    FORBIDDEN(403, 1008, "접근 권한이 없습니다."),

    USER_NOT_FOUND(404, 2001, "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(401, 2002, "비밀번호가 일치하지 않습니다.");

    private final int status;
    private final int code;
    private final String message;
}
