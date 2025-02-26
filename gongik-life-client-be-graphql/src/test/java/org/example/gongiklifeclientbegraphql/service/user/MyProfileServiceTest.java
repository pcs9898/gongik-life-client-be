package org.example.gongiklifeclientbegraphql.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.user.me.MyProfileResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MyProfileServiceTest {

  private static final String TEST_USER_ID = "test-user-id";

  @Mock
  private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

  @InjectMocks
  private MyProfileService myProfileService;

  @Test
  @DisplayName("내 프로필 조회 성공")
  void myProfile_Success() {
    // Given
    MyProfileRequest protoRequest = MyProfileRequest.newBuilder()
        .setUserId(TEST_USER_ID)
        .build();

    MyProfileResponse protoResponse = MyProfileResponse.newBuilder()
        // 필요한 프로필 정보 설정
        .build();

    when(userBlockingStub.myProfile(eq(protoRequest)))
        .thenReturn(protoResponse);

    // When
    MyProfileResponseDto response = myProfileService.myProfile(TEST_USER_ID);

    // Then
    assertNotNull(response);
    verify(userBlockingStub).myProfile(eq(protoRequest));
  }

  @Test
  @DisplayName("userId가 null일 경우 RuntimeException 발생")
  void myProfile_WhenUserIdIsNull() {
    // When & Then
    Exception exception = assertThrows(RuntimeException.class,
        () -> myProfileService.myProfile(null));

    assertEquals("Error occurred in MyProfileService : userId must not be null",
        exception.getMessage());
  }

  @Test
  @DisplayName("gRPC 서버 내부 에러 발생 시 예외 처리")
  void myProfile_WhenGrpcInternalError() {
    // Given
    when(userBlockingStub.myProfile(any(MyProfileRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.INTERNAL));

    // When & Then
    assertThrows(RuntimeException.class,
        () -> myProfileService.myProfile(TEST_USER_ID));
  }

  @Test
  @DisplayName("사용자를 찾을 수 없는 경우 예외 처리")
  void myProfile_WhenUserNotFound() {
    // Given
    when(userBlockingStub.myProfile(any(MyProfileRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

    // When & Then
    assertThrows(RuntimeException.class,
        () -> myProfileService.myProfile(TEST_USER_ID));
  }
}
