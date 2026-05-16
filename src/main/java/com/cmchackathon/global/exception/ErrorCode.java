package com.cmchackathon.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    INTERNAL_SERVER_ERROR(500, 1001, "서버 오류가 발생했습니다."),
    NOT_FOUND(404, 1002, "요청한 리소스를 찾을 수 없습니다."),
    INVALID_INPUT(400, 1003, "잘못된 입력입니다."),
    MISSING_PARAMETER(400, 1004, "필수 파라미터가 누락되었습니다."),
    TYPE_MISMATCH(400, 1005, "파라미터 타입이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(405, 1006, "허용되지 않은 HTTP 메서드입니다."),
    UNAUTHORIZED(401, 1007, "인증이 필요합니다."),
    FORBIDDEN(403, 1008, "접근 권한이 없습니다."),

    // 유저
    USER_NOT_FOUND(404, 2001, "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(401, 2002, "비밀번호가 일치하지 않습니다."),
    DUPLICATE_LOGIN_ID(409, 2003, "이미 사용 중인 아이디입니다."),
    DUPLICATE_NICKNAME(409, 2004, "이미 사용 중인 닉네임입니다."),

    // 영화관
    THEATER_NOT_FOUND(404, 3001, "영화관을 찾을 수 없습니다."),
    ALREADY_SAVED_THEATER(409, 3002, "이미 저장한 영화관입니다."),

    // 영화 / 티켓
    MOVIE_NOT_FOUND(404, 4001, "영화를 찾을 수 없습니다."),
    TICKET_NOT_FOUND(404, 5001, "티켓을 찾을 수 없습니다.");

    private final int status;
    private final int code;
    private final String message;
}
