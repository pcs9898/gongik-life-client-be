package org.example.gongiklifeclientbeauthservice.controller;

import dto.UserToUser.UserLoginHistoryRequestDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeauthservice.dto.RefreshAccessTokenResponseDto;
import org.example.gongiklifeclientbeauthservice.dto.ServiceSignInResponseDto;
import org.example.gongiklifeclientbeauthservice.dto.SignInResponseDto;
import org.example.gongiklifeclientbeauthservice.dto.SigninRequestDto;
import org.example.gongiklifeclientbeauthservice.dto.ValidateAccessTokenRequestDto;
import org.example.gongiklifeclientbeauthservice.dto.ValidateAccessTokenResponseDto;
import org.example.gongiklifeclientbeauthservice.dto.response.Response;
import org.example.gongiklifeclientbeauthservice.producer.UserLoginHistoryProducer;
import org.example.gongiklifeclientbeauthservice.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final UserLoginHistoryProducer userLoginHistoryProducer;
  private final AuthService authService;

  @PostMapping("/signIn")
  public Response<SignInResponseDto> signIn(
      @RequestBody SigninRequestDto signinRequest,
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    try {
      ServiceSignInResponseDto serviceSignInResponse = authService.signIn(signinRequest);

      String ipAddress = getClientIpAddress(request);

      log.info("serviceSignInResponse.getUser().getId()", serviceSignInResponse.getUser().getId());
      UserLoginHistoryRequestDto userLoginHistoryRequestDto = UserLoginHistoryRequestDto.builder()
          .userId(serviceSignInResponse.getUser().getId())
          .ipAddress(ipAddress)
          .build();

      userLoginHistoryProducer.sendUserLoginHistoryRequest(userLoginHistoryRequestDto);

      response.addHeader("refreshToken", serviceSignInResponse.getRefreshToken());

      return Response.success((SignInResponseDto.builder()
          .user(serviceSignInResponse.getUser())
          .accessToken(serviceSignInResponse.getAccessToken())
          .accessTokenExpiresAt(serviceSignInResponse.getAccessTokenExpiresAt())
          .build()));
    } catch (Exception e) {
      log.error("error", e);
      throw e;
    }

  }

  @PostMapping("/validateAccessToken")
  public Response<ValidateAccessTokenResponseDto> validateAccessToken(
      @RequestBody ValidateAccessTokenRequestDto request
  ) {
    try {

      return Response.success(ValidateAccessTokenResponseDto.builder()
          .userId(authService.validateAccessToken(request.getAccessToken())).build());
    } catch (Exception e) {
      log.error("validateAccessToken error", e);
      throw e;
    }
  }

  @PostMapping("/refreshAccessToken")
  public Response<RefreshAccessTokenResponseDto> refreshAccessToken(
      HttpServletRequest request
  ) {
    try {
      String refreshToken = extractRefreshTokenFromCookie(request);
      if (refreshToken == null) {
        throw new RuntimeException("refreshToken not found");
      }

      return Response.success(authService.refreshAccessToken(refreshToken));
    } catch (Exception e) {
      log.error("refreshAccessToken error", e);
      throw e;
    }
  }

  @PostMapping("/signOut")
  public Response<Void> signOut(
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    try {
      String refreshToken = extractRefreshTokenFromCookie(request);
      if (refreshToken == null) {
        throw new RuntimeException("refreshToken not found");
      }

      authService.signOut(refreshToken);

      Cookie cookie = new Cookie("refreshToken", null);
      cookie.setMaxAge(0);
      response.addCookie(cookie);

      return Response.success();
    } catch (Exception e) {
      log.error("signOut error", e);
      throw e;
    }
  }

  private String getClientIpAddress(HttpServletRequest request) {
    String ipAddress = request.getHeader("X-Forwarded-For");
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getRemoteAddr();
    }
    return ipAddress;
  }

  private String extractRefreshTokenFromCookie(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("refreshToken".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

}
