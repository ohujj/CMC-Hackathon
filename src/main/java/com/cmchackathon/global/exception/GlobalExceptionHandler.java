package com.cmchackathon.global.exception;

import com.cmchackathon.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e, HttpServletRequest req) {
        ErrorCode ec = e.getErrorCode();
        log.warn("[BusinessException] {} {} - {}", req.getMethod(), req.getRequestURI(), e.getMessage());
        return ResponseEntity.status(ec.getStatus()).body(ApiResponse.fail(ec.getCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException e, HttpServletRequest req) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err -> {
            String field = (err instanceof FieldError fe) ? fe.getField() : err.getObjectName();
            errors.put(field, err.getDefaultMessage());
        });
        log.warn("[Validation] {} {} - {}", req.getMethod(), req.getRequestURI(), errors);
        ErrorCode ec = ErrorCode.INVALID_INPUT;
        return ResponseEntity.status(ec.getStatus()).body(ApiResponse.fail(ec.getCode(), ec.getMessage(), errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraint(ConstraintViolationException e, HttpServletRequest req) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> v : e.getConstraintViolations()) {
            String path = v.getPropertyPath().toString();
            errors.put(path.substring(path.lastIndexOf('.') + 1), v.getMessage());
        }
        log.warn("[ConstraintViolation] {} {} - {}", req.getMethod(), req.getRequestURI(), errors);
        ErrorCode ec = ErrorCode.INVALID_INPUT;
        return ResponseEntity.status(ec.getStatus()).body(ApiResponse.fail(ec.getCode(), ec.getMessage(), errors));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException e, HttpServletRequest req) {
        String msg = String.format("필수 파라미터 '%s'가 누락되었습니다.", e.getParameterName());
        log.warn("[MissingParameter] {} {} - {}", req.getMethod(), req.getRequestURI(), msg);
        ErrorCode ec = ErrorCode.MISSING_PARAMETER;
        return ResponseEntity.status(ec.getStatus()).body(ApiResponse.fail(ec.getCode(), msg));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest req) {
        String msg = String.format("파라미터 '%s'의 타입이 올바르지 않습니다.", e.getName());
        log.warn("[TypeMismatch] {} {} - {}", req.getMethod(), req.getRequestURI(), msg);
        ErrorCode ec = ErrorCode.TYPE_MISMATCH;
        return ResponseEntity.status(ec.getStatus()).body(ApiResponse.fail(ec.getCode(), msg));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(HttpMessageNotReadableException e, HttpServletRequest req) {
        log.warn("[NotReadable] {} {} - {}", req.getMethod(), req.getRequestURI(), e.getMessage());
        ErrorCode ec = ErrorCode.INVALID_INPUT;
        return ResponseEntity.status(ec.getStatus()).body(ApiResponse.fail(ec.getCode(), "요청 본문을 읽을 수 없습니다."));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e, HttpServletRequest req) {
        log.warn("[MethodNotAllowed] {} {} - {}", req.getMethod(), req.getRequestURI(), e.getMethod());
        ErrorCode ec = ErrorCode.METHOD_NOT_ALLOWED;
        return ResponseEntity.status(ec.getStatus()).body(ApiResponse.fail(ec.getCode(), ec.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResource(NoResourceFoundException e, HttpServletRequest req) {
        log.warn("[NotFound] {} {}", req.getMethod(), req.getRequestURI());
        ErrorCode ec = ErrorCode.NOT_FOUND;
        return ResponseEntity.status(ec.getStatus()).body(ApiResponse.fail(ec.getCode(), ec.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e, HttpServletRequest req) {
        log.error("[UnhandledException] {} {} - {}", req.getMethod(), req.getRequestURI(), e.getMessage(), e);
        ErrorCode ec = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(ec.getStatus()).body(ApiResponse.fail(ec.getCode(), ec.getMessage()));
    }
}