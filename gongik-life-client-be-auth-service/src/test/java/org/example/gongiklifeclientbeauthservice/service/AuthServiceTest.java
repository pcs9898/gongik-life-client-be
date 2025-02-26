package org.example.gongiklifeclientbeauthservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dto.user.UserLoginHistoryRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.gongiklifeclientbeauthservice.dto.RefreshAccessTokenResponseDto;
import org.example.gongiklifeclientbeauthservice.dto.ServiceSignInResponseDto;
import org.example.gongiklifeclientbeauthservice.dto.SignInUserDto;
import org.example.gongiklifeclientbeauthservice.dto.SigninRequestDto;
import org.example.gongiklifeclientbeauthservice.dto.TokenDto;
import org.example.gongiklifeclientbeauthservice.model.CustomUserDetails;
import org.example.gongiklifeclientbeauthservice.producer.UserLoginHistoryProducer;
import org.example.gongiklifeclientbeauthservice.provider.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private JwtTokenProvider tokenProvider;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private UserLoginHistoryProducer userLoginHistoryProducer;

  @Mock
  private RedisTemplate<String, String> redisTemplate;

  @InjectMocks
  private AuthService authService;

  // 테스트를 위한 refresh token 유효 기간 값 (예: 1시간)
  @BeforeEach
  void setUp() {
    authService.refreshTokenValidityInMilliseconds = 3600000L;
  }

  @Test
  void signInTest() {
    // given
    SigninRequestDto requestDto = new SigninRequestDto();
    requestDto.setEmail("test@test.com");
    requestDto.setPassword("password");

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    // 사용자 인증을 위한 모킹 설정
    CustomUserDetails userDetails = mock(CustomUserDetails.class);
    when(userDetails.getId()).thenReturn("user1");
    // 로그인 성공 시 반환될 사용자 DTO (toSignInUserDto)
    SignInUserDto signInUserDto = new SignInUserDto();
    when(userDetails.toSignInUserDto()).thenReturn(signInUserDto);

    Authentication authentication = mock(Authentication.class);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userDetails);

    // 토큰 생성 모킹
    TokenDto accessTokenDto = TokenDto.builder()
        .accessToken("ACCESS_TOKEN")
        .accessTokenExpiresAt("123456789")
        .build();
    when(tokenProvider.generateAccessToken("user1")).thenReturn(
        TokenDto.builder()
            .accessToken("ACCESS_TOKEN")
            .accessTokenExpiresAt("123456789")
            .build()
    );
    when(tokenProvider.generateRefreshToken("user1")).thenReturn("REFRESH_TOKEN");

    // Redis ValueOperations 모킹
    ValueOperations<String, String> valueOps = mock(ValueOperations.class);
    when(redisTemplate.opsForValue()).thenReturn(valueOps);

    // when
    ServiceSignInResponseDto result = authService.signIn(requestDto, request, response);

    // then
    assertNotNull(result);
    assertEquals("ACCESS_TOKEN", result.getAccessToken());
    assertEquals("REFRESH_TOKEN", result.getRefreshToken());
    assertEquals("123456789", result.getAccessTokenExpiresAt());
    assertSame(signInUserDto, result.getUser());

    // 응답 헤더에 refreshToken이 추가되었는지 검증
    verify(response).addHeader("refreshToken", "REFRESH_TOKEN");
    // 로그인 히스토리 프로듀서의 sendUserLoginHistoryRequest 호출 검증
    verify(userLoginHistoryProducer).sendUserLoginHistoryRequest(
        any(UserLoginHistoryRequestDto.class));
  }

  @Test
  void refreshAccessTokenTest() {
    // given
    String oldRefreshToken = "OLD_REFRESH_TOKEN";
    String userId = "user1";

    // tokenProvider 검증 모킹
    when(tokenProvider.validateRefreshTokenAndGetId(oldRefreshToken)).thenReturn(userId);

    // Redis 에서 저장된 token 반환 모킹
    ValueOperations<String, String> valueOps = mock(ValueOperations.class);
    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(valueOps.get("user:" + userId + ":refreshToken")).thenReturn(oldRefreshToken);

    // 새로운 access token 생성 모킹
    TokenDto newToken = TokenDto.builder()
        .accessToken("NEW_ACCESS_TOKEN")
        .accessTokenExpiresAt("123456789")
        .build();
    when(tokenProvider.generateAccessToken(userId)).thenReturn(newToken);

    // when
    RefreshAccessTokenResponseDto responseDto = authService.refreshAccessToken(oldRefreshToken);

    // then
    assertNotNull(responseDto);
    assertEquals("NEW_ACCESS_TOKEN", responseDto.getAccessToken());
    assertEquals("123456789", responseDto.getAccessTokenExpiresAt());
  }

  @Test
  void signOutTest() {
    // given
    String refreshToken = "REFRESH_TOKEN";
    String userId = "user1";

    when(tokenProvider.validateRefreshTokenAndGetId(refreshToken)).thenReturn(userId);

    ValueOperations<String, String> valueOps = mock(ValueOperations.class);
    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(valueOps.get("user:" + userId + ":refreshToken")).thenReturn(refreshToken);

    HttpServletResponse response = mock(HttpServletResponse.class);

    // when
    authService.signOut(refreshToken, response);

    // then
    // redisTemplate에서 삭제 호출
    verify(redisTemplate).delete("user:" + userId + ":refreshToken");
    // 응답 쿠키에 refreshToken 제거용 쿠키가 추가되었는지 확인 (maxAge==0)
    verify(response).addCookie(argThat(cookie ->
        "refreshToken".equals(cookie.getName()) && cookie.getMaxAge() == 0
    ));
  }

  @Test
  public void testValidateAccessToken() {
    // Given
    String accessToken = "VALID_ACCESS_TOKEN";
    String expectedUserId = "user1";
    when(tokenProvider.validateAccessTokenAndGetId(accessToken)).thenReturn(expectedUserId);

    // When
    String actualUserId = authService.validateAccessToken(accessToken);

    // Then
    assertEquals(expectedUserId, actualUserId);
    verify(tokenProvider, times(1)).validateAccessTokenAndGetId(accessToken);
  }
}
