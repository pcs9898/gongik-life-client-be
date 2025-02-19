package org.example.gongiklifeclientbeauthservice.exception;


import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeauthservice.dto.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

  @ExceptionHandler(AuthServiceException.class)
  public ResponseEntity<?> authServiceErrorHandler(AuthServiceException e) {
    log.error("Auth Service Error: {}", e.toString());
    return ResponseEntity.status(e.getErrorCode().getStatus())
        .body(Response.error(e.getErrorCode().name()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> illegalArgumentErrorHandler(IllegalArgumentException e) {
    log.error("Illegal Argument Error: {}", e.toString());
    return ResponseEntity.status(ErrorCode.BAD_REQUEST.getStatus())
        .body(Response.error(ErrorCode.BAD_REQUEST.name()));
  }


  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<?> badCredentialsErrorHandler(BadCredentialsException e) {
    log.error("Bad Credentials Error: {}", e.toString());
    return ResponseEntity.status(ErrorCode.INVALID_PASSWORD.getStatus())
        .body(Response.error(ErrorCode.INVALID_PASSWORD.getMessage()));
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<?> ioErrorHandler(IOException e) {
    log.error("IO Error: {}", e.toString());
    return ResponseEntity.status(ErrorCode.IO_ERROR.getStatus())
        .body(Response.error(ErrorCode.IO_ERROR.name()));
  }


  @ExceptionHandler(InternalAuthenticationServiceException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleInternalAuthenticationServiceException(
      InternalAuthenticationServiceException ex) {

    System.err.println("InternalAuthenticationServiceException: " + ex.getMessage());
    return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
        .body(Response.error(ex.getMessage())).toString();


  }

  // 마지막으로 처리되지 않은 모든 예외를 처리
  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleAllExceptions(Exception e) {
    log.error("Unexpected Error occurred: ", e);
    return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
        .body(Response.error(e.getMessage()));
  }


}




