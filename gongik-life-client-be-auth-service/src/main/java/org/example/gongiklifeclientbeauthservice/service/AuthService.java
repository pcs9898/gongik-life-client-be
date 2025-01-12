package org.example.gongiklifeclientbeauthservice.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeauthservice.dto.RefreshAccessTokenResponseDto;
import org.example.gongiklifeclientbeauthservice.dto.ServiceSignInResponseDto;
import org.example.gongiklifeclientbeauthservice.dto.SigninRequestDto;
import org.example.gongiklifeclientbeauthservice.dto.TokenDto;
import org.example.gongiklifeclientbeauthservice.model.CustomUserDetails;
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

  @Value("${jwt.refresh-token-validity}")
  private long refreshTokenValidityInMilliseconds;

  private final JwtTokenProvider tokenProvider;
  private final AuthenticationManager authenticationManager;
  private final RedisTemplate<String, String> redisTemplate;


  public ServiceSignInResponseDto signIn(SigninRequestDto request) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    TokenDto tokenInfos = generateTokenAndSaveRefreshToken(userDetails.getId());

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

  public String validateAccessToken(String token) {
    return tokenProvider.validateAccessTokenAndGetId(token);
  }

  public RefreshAccessTokenResponseDto refreshAccessToken(String refreshToken) {

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

}
