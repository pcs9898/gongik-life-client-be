package org.example.gongiklifeclientbegraphql.service.user;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeResponse;
import org.example.gongiklifeclientbegraphql.dto.user.verifyEmailCode.VerifyEmailCodeRequestDto;
import org.example.gongiklifeclientbegraphql.dto.user.verifyEmailCode.VerifyEmailCodeResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VerifyEmailCodeServiceTest {

  @Mock
  private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

  @InjectMocks
  private VerifyEmailCodeService verifyEmailCodeService;

  private VerifyEmailCodeRequestDto requestDto;
  private VerifyEmailCodeResponse successResponse;

  @BeforeEach
  void setUp() {
    // 테스트용 요청 DTO 생성
    requestDto = VerifyEmailCodeRequestDto.builder()
        .email("test@example.com")
        .code("123456")
        .build();

    // 성공 응답 생성
    successResponse = VerifyEmailCodeResponse.newBuilder()
        .setSuccess(true)
        .build();
  }

  @Nested
  @DisplayName("verifyEmailCode 메소드는")
  class VerifyEmailCodeMethod {

    @Test
    @DisplayName("성공: 올바른 이메일과 코드로 요청시 성공 응답을 반환한다")
    void success_whenValidEmailAndCode_returnsSuccessResponse() {
      // given
      when(userBlockingStub.verifyEmailCode(any(VerifyEmailCodeRequest.class)))
          .thenReturn(successResponse);

      // when
      VerifyEmailCodeResponseDto result = verifyEmailCodeService.verifyEmailCode(requestDto);

      // then
      assertAll(
          () -> assertNotNull(result),
          () -> assertTrue(result.isSuccess())
      );
    }

    @Test
    @DisplayName("실패: gRPC 호출 실패시 예외를 던진다")
    void fail_whenGrpcCallFails_throwsException() {
      // given
      when(userBlockingStub.verifyEmailCode(any(VerifyEmailCodeRequest.class)))
          .thenThrow(new RuntimeException("gRPC call failed"));

      // when & then
      assertThrows(RuntimeException.class,
          () -> verifyEmailCodeService.verifyEmailCode(requestDto));
    }

    @Test
    @DisplayName("실패: 잘못된 인증 코드로 요청시 실패 응답을 반환한다")
    void fail_whenInvalidCode_returnsFailureResponse() {
      // given
      VerifyEmailCodeResponse failureResponse = VerifyEmailCodeResponse.newBuilder()
          .setSuccess(false)
          .build();

      when(userBlockingStub.verifyEmailCode(any(VerifyEmailCodeRequest.class)))
          .thenReturn(failureResponse);

      // when
      VerifyEmailCodeResponseDto result = verifyEmailCodeService.verifyEmailCode(requestDto);

      // then
      assertFalse(result.isSuccess());
    }
  }
}
