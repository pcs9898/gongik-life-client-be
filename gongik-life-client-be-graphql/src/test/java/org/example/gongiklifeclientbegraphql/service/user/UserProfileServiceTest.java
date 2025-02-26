package org.example.gongiklifeclientbegraphql.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.UserProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.UserProfileResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.user.userProfile.UserProfileResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

  private static final String TEST_USER_ID = "test-user-id";

  @Mock
  private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

  @InjectMocks
  private UserProfileService userProfileService;

  @Test
  @DisplayName("사용자 프로필 조회 성공")
  void userProfile_Success() {
    // Given
    UserProfileRequest protoRequest = UserProfileRequest.newBuilder()
        .setUserId(TEST_USER_ID)
        .build();

    UserProfileResponse protoResponse = UserProfileResponse.newBuilder()
        // 필요한 프로필 정보 설정
        .build();

    when(userBlockingStub.userProfile(eq(protoRequest)))
        .thenReturn(protoResponse);

    // When
    UserProfileResponseDto response = userProfileService.userProfile(TEST_USER_ID);

    // Then
    assertNotNull(response);
    verify(userBlockingStub).userProfile(eq(protoRequest));
  }

  @Test
  @DisplayName("userId가 null일 경우 RuntimeException 발생")
  void userProfile_WhenUserIdIsNull() {
    // When & Then
    Exception exception = assertThrows(RuntimeException.class,
        () -> userProfileService.userProfile(null));

    assertEquals("userId must not be null",
        exception.getMessage());
  }

  @Test
  @DisplayName("gRPC 서버 내부 에러 발생 시 예외 처리")
  void userProfile_WhenGrpcInternalError() {
    // Given
    when(
        userBlockingStub.userProfile(any(UserProfileRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.INTERNAL));

    // When & Then
    assertThrows(RuntimeException.class,
        () -> userProfileService.userProfile(TEST_USER_ID));
  }

  @Test
  @DisplayName("사용자를 찾을 수 없는 경우 예외 처리")
  void userProfile_WhenUserNotFound() {
    // Given
    when(userBlockingStub.userProfile(any(UserProfileRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

    // When & Then
    assertThrows(RuntimeException.class,
        () -> userProfileService.userProfile(TEST_USER_ID));
  }
}
