package com.todoay.api.global.config;

import com.todoay.api.global.exception.AbstractApiException;
import com.todoay.api.global.exception.ErrorResponse;
import com.todoay.api.global.exception.GlobalErrorCode;
import com.todoay.api.global.exception.ValidErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLIntegrityConstraintViolationException;

import static com.todoay.api.global.exception.GlobalErrorCode.*;
import static com.todoay.api.global.exception.GlobalErrorCode.SQL_INTEGRITY_CONSTRAINT_VIOLATION;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(AbstractApiException.class)
    public ResponseEntity<ErrorResponse> handleAbstractApiException(AbstractApiException e, HttpServletRequest request) {
        return ErrorResponse.toResponseEntity(e, request.getRequestURI());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ValidErrorResponse> handleBindException(BindException ex, HttpServletRequest request) {
        return ValidErrorResponse.toResponseEntity(ex, request.getRequestURI());
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleSqlIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex, HttpServletRequest request) {
        return ErrorResponse.toResponseEntity(SQL_INTEGRITY_CONSTRAINT_VIOLATION, request.getRequestURI());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(HttpServletRequest request) {
        return ErrorResponse.toResponseEntity(JWT_EXPIRED, request.getRequestURI());
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleSignatureException(HttpServletRequest request) {
        return ErrorResponse.toResponseEntity(JWT_NOT_VERIFIED, request.getRequestURI());
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwtException(HttpServletRequest request) {
        return ErrorResponse.toResponseEntity(JWT_MALFORMED, request.getRequestURI());
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedJwtException(HttpServletRequest request) {
        return ErrorResponse.toResponseEntity(JWT_UNSUPPORTED, request.getRequestURI());
    }

}
