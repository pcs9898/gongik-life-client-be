package org.example.gongiklifeclientbegraphql.service.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.user.sendEmailVerificationCode.SendEmailVerificationCodeRequestDto;
import org.example.gongiklifeclientbegraphql.dto.user.sendEmailVerificationCode.SendEmailVerificationCodeResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SendEmailVerificationCodeServiceTest {

  private static final String TEST_EMAIL = "test@example.com";
  @Mock
  private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;
  @InjectMocks
  private SendEmailVerificationCodeService sendEmailVerificationCodeService;

  @Test
  @DisplayName("이메일 인증 코드 발송 성공")
  void sendEmailVerificationCode_Success() {
    // Given
    SendEmailVerificationCodeRequestDto requestDto =
        new SendEmailVerificationCodeRequestDto(TEST_EMAIL);

    SendEmailVerificationCodeRequest protoRequest =
        SendEmailVerificationCodeRequest.newBuilder()
            .setEmail(TEST_EMAIL)
            .build();

    SendEmailVerificationCodeResponse protoResponse =
        SendEmailVerificationCodeResponse.newBuilder()
            .setSuccess(true)
            .build();

    when(userBlockingStub.sendEmailVerificationCode(eq(protoRequest)))
        .thenReturn(protoResponse);

    // When
    SendEmailVerificationCodeResponseDto response =
        sendEmailVerificationCodeService.sendEmailVerificationCode(requestDto);

    // Then
    assertTrue(response.isSuccess());
    verify(userBlockingStub).sendEmailVerificationCode(eq(protoRequest));
  }

  private SendEmailVerificationCodeRequest any(
      Class<SendEmailVerificationCodeRequest> sendEmailVerificationCodeRequestClass) {
    return null;
  }


  @Test
  @DisplayName("gRPC 서버 내부 에러 발생 시 예외 처리")
  void sendEmailVerificationCode_WhenGrpcInternalError() {
    // Given
    SendEmailVerificationCodeRequestDto requestDto =
        new SendEmailVerificationCodeRequestDto(TEST_EMAIL);

    when(userBlockingStub.sendEmailVerificationCode(
        any(SendEmailVerificationCodeRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.INTERNAL));

    // When & Then
    assertThrows(RuntimeException.class,
        () -> sendEmailVerificationCodeService.sendEmailVerificationCode(requestDto));
  }

  @Test
  @DisplayName("이미 존재하는 이메일로 인한 실패")
  void sendEmailVerificationCode_WhenEmailAlreadyExists() {
    // Given
    SendEmailVerificationCodeRequestDto requestDto =
        new SendEmailVerificationCodeRequestDto(TEST_EMAIL);

    when(userBlockingStub.sendEmailVerificationCode(
        any(SendEmailVerificationCodeRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.ALREADY_EXISTS));

    // When & Then
    assertThrows(RuntimeException.class,
        () -> sendEmailVerificationCodeService.sendEmailVerificationCode(requestDto));
  }

  @Test
  @DisplayName("잘못된 이메일 형식으로 인한 실패")
  void sendEmailVerificationCode_WhenInvalidEmailFormat() {
    // Given
    SendEmailVerificationCodeRequestDto requestDto =
        new SendEmailVerificationCodeRequestDto("invalid-email");

    when(userBlockingStub.sendEmailVerificationCode(
        any(SendEmailVerificationCodeRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.INVALID_ARGUMENT));

    // When & Then
    assertThrows(RuntimeException.class,
        () -> sendEmailVerificationCodeService.sendEmailVerificationCode(requestDto));
  }
}
