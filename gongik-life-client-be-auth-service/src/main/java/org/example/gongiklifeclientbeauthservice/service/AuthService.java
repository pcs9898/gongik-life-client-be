package org.example.gongiklifeclientbeauthservice.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        .accessTokenExpiresAt(tokenInfos.getAccessTokenExpiresAt().toString())
        .build();

  }

  public TokenDto generateTokenAndSaveRefreshToken(String id) {
    // 1. 토큰 생성
    TokenDto accessTokenDto = tokenProvider.generateAccessToken(id);
    String refreshToken = tokenProvider.generateRefreshToken(id);
    // 2. Refresh 토큰 저장
    saveRefreshToken(id, refreshToken);

    return TokenDto.builder()
        .accessToken(accessTokenDto.getAccessToken())
        .refreshToken(refreshToken)
        .accessTokenExpiresAt(accessTokenDto.getAccessTokenExpiresAt())
        .build();
  }

  public boolean validateToken(String token) {
    return tokenProvider.validateToken(token);
  }

//  public TokenDto refreshAccessToken(String refreshToken) {
//    // 1. Refresh 토큰 검증
//    if (!tokenProvider.validateToken(refreshToken)) {
//      throw new InvalidTokenException("Invalid refresh token");
//    }
//
//    // 2. 토큰에서 사용자 정보 추출
//    String email = tokenProvider.getEmailFromToken(refreshToken);
//
//    // 3. DB의 Refresh 토큰과 비교
//    RefreshToken savedToken = refreshTokenRepository.findByEmail(email)
//        .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));
//
//    if (!savedToken.getToken().equals(refreshToken)) {
//      throw new InvalidTokenException("Refresh token mismatch");
//    }
//
//    // 4. 새로운 액세스 토큰 발급
//    String newAccessToken = tokenProvider.generateAccessToken(email);
//
//    return TokenDto.builder()
//        .accessToken(newAccessToken)
//        .refreshToken(refreshToken)
//        .accessTokenExpiresIn(tokenProvider.getAccessTokenExpiresIn())
//        .build();
//  }

//  public void signout(String accessToken) {
//    String email = tokenProvider.getEmailFromToken(accessToken);
//    refreshTokenRepository.deleteByEmail(email);
//  }

  private void saveRefreshToken(String email, String refreshToken) {
    String key = "user:" + email + ":refreshToken";
    redisTemplate.opsForValue()
        .set(key, refreshToken, refreshTokenValidityInMilliseconds, TimeUnit.MILLISECONDS);
  }
}
