package org.example.gongiklifeclientbeauthservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not founded"),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "Email not founded"),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "Post not founded"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "Invalid password"),
    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "Duplicated user name"),
    ALREADY_LIKED_POST(HttpStatus.CONFLICT, "user already like the post"),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "User has invalid permission"),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Database error occurs"),
    NOTIFICATION_CONNECT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "Connect to notification occurs error"),
    // 추가할 에러 코드
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized access"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden access"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed"),
    CONFLICT(HttpStatus.CONFLICT, "Resource conflict"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Service temporarily unavailable"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation error"),
    IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "IO operation failed");


    private final HttpStatus status;
    private final String message;
}
