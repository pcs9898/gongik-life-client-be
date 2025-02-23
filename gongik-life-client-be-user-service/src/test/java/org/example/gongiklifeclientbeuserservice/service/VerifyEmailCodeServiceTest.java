package org.example.gongiklifeclientbeuserservice.service;

import static org.example.gongiklifeclientbeuserservice.constants.UserServiceConstants.EXPIRATION_MINUTES;
import static org.example.gongiklifeclientbeuserservice.constants.UserServiceConstants.VERIFICATION_CODE_PREFIX;
import static org.example.gongiklifeclientbeuserservice.constants.UserServiceConstants.VERIFIED_EMAIL_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class VerifyEmailCodeServiceTest {

  private static final String TEST_EMAIL = "test@example.com";
  private static final String TEST_CODE = "123456";
  //  private static final String VERIFIED_EMAIL_PREFIX = "verified_email::";
  @Mock
  private RedisTemplate<String, String> redisTemplate;
  @Mock
  private UserService userService;
  @Mock
  private ValueOperations<String, String> valueOperations;
  @InjectMocks
  private VerifyEmailCodeService verifyEmailCodeService;

  @BeforeEach
  void setUp() {
    lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
  }

  @Test
  @DisplayName("이메일 인증 코드 검증 성공")
  void verifyEmailCode_Success() {
    // Given
    VerifyEmailCodeRequest request = VerifyEmailCodeRequest.newBuilder()
        .setEmail(TEST_EMAIL)
        .setCode(TEST_CODE)
        .build();

    when(userService.isEmailVerified(TEST_EMAIL)).thenReturn(false);
    when(valueOperations.get(VERIFICATION_CODE_PREFIX + TEST_EMAIL)).thenReturn(TEST_CODE);

    // When
    VerifyEmailCodeResponse response = verifyEmailCodeService.verifyEmailCode(request);

    // Then
    assertTrue(response.getSuccess());
    verify(redisTemplate).delete(VERIFICATION_CODE_PREFIX + TEST_EMAIL);
    verify(valueOperations).set(
        eq(VERIFIED_EMAIL_PREFIX + TEST_EMAIL),
        eq("true"),
        eq(EXPIRATION_MINUTES),
        eq(TimeUnit.MINUTES)
    );
  }

  @Test
  @DisplayName("이미 인증된 이메일 검증 시도 시 예외 발생")
  void verifyEmailCode_AlreadyVerified() {
    // Given
    VerifyEmailCodeRequest request = VerifyEmailCodeRequest.newBuilder()
        .setEmail(TEST_EMAIL)
        .setCode(TEST_CODE)
        .build();

    when(userService.isEmailVerified(TEST_EMAIL)).thenReturn(true);

    // When & Then
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
        () -> verifyEmailCodeService.verifyEmailCode(request));

    assertEquals(Status.ALREADY_EXISTS.getCode(), exception.getStatus().getCode());
  }

  @Test
  @DisplayName("만료된 인증 코드로 검증 시도 시 예외 발생")
  void verifyEmailCode_ExpiredCode() {
    // Given
    VerifyEmailCodeRequest request = VerifyEmailCodeRequest.newBuilder()
        .setEmail(TEST_EMAIL)
        .setCode(TEST_CODE)
        .build();

    when(userService.isEmailVerified(TEST_EMAIL)).thenReturn(false);
    when(valueOperations.get(VERIFICATION_CODE_PREFIX + TEST_EMAIL)).thenReturn(null);

    // When & Then
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
        () -> verifyEmailCodeService.verifyEmailCode(request));

    assertEquals(Status.NOT_FOUND.getCode(), exception.getStatus().getCode());
  }

  @Test
  @DisplayName("잘못된 인증 코드로 검증 시도 시 예외 발생")
  void verifyEmailCode_InvalidCode() {
    // Given
    String wrongCode = "654321";
    VerifyEmailCodeRequest request = VerifyEmailCodeRequest.newBuilder()
        .setEmail(TEST_EMAIL)
        .setCode(wrongCode)
        .build();

    when(userService.isEmailVerified(TEST_EMAIL)).thenReturn(false);
    when(valueOperations.get(VERIFICATION_CODE_PREFIX + TEST_EMAIL)).thenReturn(TEST_CODE);

    // When & Then
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
        () -> verifyEmailCodeService.verifyEmailCode(request));

    assertEquals(Status.INVALID_ARGUMENT.getCode(), exception.getStatus().getCode());
  }
}