package org.example.gongiklifeclientbeuserservice.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import org.example.gongiklifeclientbeuserservice.constants.UserServiceConstants;
import org.example.gongiklifeclientbeuserservice.exception.SendEmailVerificationException;
import org.example.gongiklifeclientbeuserservice.producer.EmailVerificationCodeProducer;
import org.example.gongiklifeclientbeuserservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class SendEmailVerificationCodeServiceTest {

  private static final String TEST_EMAIL = "test@example.com";
  private static final String TEST_CODE = "123456";
  @Mock
  private CommonUserService commonUserService;
  @Mock
  private RedisTemplate<String, String> redisTemplate;
  @Mock
  private EmailVerificationCodeProducer emailVerificationCodeProducer;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ValueOperations<String, String> valueOperations;

  @InjectMocks
  private SendEmailVerificationCodeService sendEmailVerificationCodeService;
  private SendEmailVerificationCodeRequest request;

  @BeforeEach
  void setUp() {
    request = SendEmailVerificationCodeRequest.newBuilder()
        .setEmail(TEST_EMAIL)
        .build();

    lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
  }

  @Nested
  @DisplayName("sendEmailVerificationCode 메소드는")
  class SendEmailVerificationCode {

    @Test
    @DisplayName("성공: 새로운 이메일에 대해 인증 코드를 발송하고 true를 반환한다")
    void success_whenNewEmail_sendsVerificationCode() {
      // given
      when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
      when(valueOperations.get(anyString())).thenReturn(null);

      // when
      SendEmailVerificationCodeResponse response =
          sendEmailVerificationCodeService.sendEmailVerificationCode(request);

      // then

      assertAll(
          () -> assertTrue(response.getSuccess()),
          () -> verify(emailVerificationCodeProducer).sendEmailVerificationRequest(any()),
          () -> verify(commonUserService).saveEmailVerificationCode(eq(TEST_EMAIL), anyString())
      );
    }

    @Test
    @DisplayName("실패: 이미 등록된 이메일인 경우 ALREADY_EXISTS 예외를 던진다")
    void fail_whenEmailAlreadyRegistered_throwsAlreadyExists() {
      // given
      when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

      // when & then
      StatusRuntimeException exception = assertThrows(
          StatusRuntimeException.class,
          () -> sendEmailVerificationCodeService.sendEmailVerificationCode(request)
      );

      assertEquals(Status.Code.ALREADY_EXISTS, exception.getStatus().getCode());
    }

    @Test
    @DisplayName("실패: 재전송 대기 시간이 지나지 않은 경우 RESOURCE_EXHAUSTED 예외를 던진다")
    void fail_whenWithinResendWaitPeriod_throwsResourceExhausted() {
      // given
      when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
      when(valueOperations.get(anyString())).thenReturn(TEST_CODE);
      when(redisTemplate.getExpire(anyString(), any(TimeUnit.class)))
          .thenReturn(UserServiceConstants.EXPIRATION_MINUTES);

      // when & then
      StatusRuntimeException exception = assertThrows(
          StatusRuntimeException.class,
          () -> sendEmailVerificationCodeService.sendEmailVerificationCode(request)
      );

      assertEquals(Status.Code.RESOURCE_EXHAUSTED, exception.getStatus().getCode());
    }

    @Test
    @DisplayName("실패: 이메일 발송 실패 시 SendEmailVerificationException을 던진다")
    void fail_whenEmailSendingFails_throwsSendEmailVerificationException() {
      // given
      when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
      when(valueOperations.get(anyString())).thenReturn(null);
      doThrow(new RuntimeException("Email sending failed"))
          .when(emailVerificationCodeProducer)
          .sendEmailVerificationRequest(any());

      // when & then
      assertThrows(
          SendEmailVerificationException.class,
          () -> sendEmailVerificationCodeService.sendEmailVerificationCode(request)
      );
    }
  }
}
