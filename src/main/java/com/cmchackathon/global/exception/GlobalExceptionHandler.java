package com.cmchackathon.global.exception;

import com.cmchackathon.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ApiResponse.fail(e.getErrorCode().getCode(), e.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(NoResourceFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(ErrorCode.NOT_FOUND.getCode(), ErrorCode.NOT_FOUND.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.fail(405, "지원하지 않는 HTTP 메서드입니다."));
    }

    // 400 - @Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(ErrorCode.INVALID_INPUT.getCode(), ErrorCode.INVALID_INPUT.getMessage()));
    }

    // 400 - @PathVariable, @RequestParam 타입 불일치
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(ErrorCode.INVALID_INPUT.getCode(), ErrorCode.INVALID_INPUT.getMessage()));
    }

    // 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(ErrorCode.INVALID_INPUT.getCode(), ErrorCode.INVALID_INPUT.getMessage()));
    }

}