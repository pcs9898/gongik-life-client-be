package org.example.gongiklifeclientbeauthservice.service;

import dto.user.UserLoginHistoryRequestDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeauthservice.dto.RefreshAccessTokenResponseDto;
import org.example.gongiklifeclientbeauthservice.dto.ServiceSignInResponseDto;
import org.example.gongiklifeclientbeauthservice.dto.SigninRequestDto;
import org.example.gongiklifeclientbeauthservice.dto.TokenDto;
import org.example.gongiklifeclientbeauthservice.model.CustomUserDetails;
import org.example.gongiklifeclientbeauthservice.producer.UserLoginHistoryProducer;
import org.example.gongiklifeclientbeauthservice.provider.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
  private static final String BEARER_PREFIX = "Bearer ";
  private final JwtTokenProvider tokenProvider;
  private final AuthenticationManager authenticationManager;
  private final UserLoginHistoryProducer userLoginHistoryProducer;
  private final RedisTemplate<String, String> redisTemplate;
  @Value("${jwt.refresh-token-validity}")
  public long refreshTokenValidityInMilliseconds;

  public ServiceSignInResponseDto signIn(SigninRequestDto request, HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    TokenDto tokenInfos = generateTokenAndSaveRefreshToken(userDetails.getId());

    recordUserLoginHistory(userDetails.toSignInUserDto().getId(), httpRequest);

    setRefreshTokenInResponse(httpResponse, tokenInfos.getRefreshToken());

    return ServiceSignInResponseDto.builder()
        .user(userDetails.toSignInUserDto())
        .accessToken(tokenInfos.getAccessToken())
        .refreshToken(tokenInfos.getRefreshToken())
        .accessTokenExpiresAt(tokenInfos.getAccessTokenExpiresAt())
        .build();

  }

  public TokenDto generateTokenAndSaveRefreshToken(String userId) {
    // 1. 토큰 생성
    TokenDto accessTokenDto = tokenProvider.generateAccessToken(userId);
    String refreshToken = tokenProvider.generateRefreshToken(userId);
    // 2. Refresh 토큰 저장
    saveRefreshToken(userId, refreshToken);

    return TokenDto.builder()
        .accessToken(accessTokenDto.getAccessToken())
        .refreshToken(refreshToken)
        .accessTokenExpiresAt(accessTokenDto.getAccessTokenExpiresAt())
        .build();
  }

  public String validateAccessToken(String accessToken) {
    return tokenProvider.validateAccessTokenAndGetId(accessToken);
  }

  public RefreshAccessTokenResponseDto refreshAccessToken(String refreshToken) {

    log.info("refreshToken@@@: {}", refreshToken);
    String userId = tokenProvider.validateRefreshTokenAndGetId(refreshToken);

    String foundRefreshToken = getRefreshToken(userId);

    if (!refreshToken.equals(foundRefreshToken)) {
      throw new RuntimeException("Refresh token mismatch");
    }

    TokenDto tokenDto = tokenProvider.generateAccessToken(userId);

    return RefreshAccessTokenResponseDto.builder()
        .accessToken(tokenDto.getAccessToken())
        .accessTokenExpiresAt(tokenDto.getAccessTokenExpiresAt())
        .build();


  }


  private void saveRefreshToken(String userId, String refreshToken) {
    String key = "user:" + userId + ":refreshToken";
    redisTemplate.opsForValue()
        .set(key, refreshToken, refreshTokenValidityInMilliseconds, TimeUnit.MILLISECONDS);
  }

  private String getRefreshToken(String userId) {
    String key = "user:" + userId + ":refreshToken";
    return redisTemplate.opsForValue().get(key);
  }

  public void signOut(String refreshToken, HttpServletResponse response) {
    String userId = tokenProvider.validateRefreshTokenAndGetId(refreshToken);

    String key = "user:" + userId + ":refreshToken";

    String foundRefreshToken = getRefreshToken(userId);

    if (foundRefreshToken == null) {
      throw new RuntimeException("Refresh token not found, already signed out");
    }

    redisTemplate.delete(key);

    removeRefreshTokenCookie(response);
  }

  private void recordUserLoginHistory(String userId, HttpServletRequest request) {
    UserLoginHistoryRequestDto historyRequest = UserLoginHistoryRequestDto.builder()
        .userId(userId)
        .ipAddress(getClientIpAddress(request))
        .build();
    userLoginHistoryProducer.sendUserLoginHistoryRequest(historyRequest);
  }

  private String getClientIpAddress(HttpServletRequest request) {
    String[] headerNames = {
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_CLIENT_IP",
        "HTTP_X_FORWARDED_FOR"
    };

    return Arrays.stream(headerNames)
        .map(request::getHeader)
        .filter(ip -> ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip))
        .findFirst()
        .orElse(request.getRemoteAddr());
  }

  private void setRefreshTokenInResponse(HttpServletResponse response, String refreshToken) {
    response.addHeader(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
  }

  private void removeRefreshTokenCookie(HttpServletResponse response) {
    Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }

}
