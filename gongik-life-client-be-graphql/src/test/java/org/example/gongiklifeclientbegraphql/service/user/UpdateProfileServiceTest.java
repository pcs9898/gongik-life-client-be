package org.example.gongiklifeclientbegraphql.service.user;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.UpdateProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.UpdateProfileResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.user.updateProfile.UpdateProfileRequestDto;
import org.example.gongiklifeclientbegraphql.dto.user.updateProfile.UpdateProfileResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UpdateProfileServiceTest {

  private static final String TEST_USER_ID = "test-user-id";

  @Mock
  private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

  @InjectMocks
  private UpdateProfileService updateProfileService;

  @BeforeEach
  void setUp() {
    // gRPC 클라이언트 수동 주입
    ReflectionTestUtils.setField(updateProfileService, "userBlockingStub", userBlockingStub);
  }

  @Test
  @DisplayName("프로필 업데이트 성공")
  void updateProfile_Success() {
    // Given
    UpdateProfileRequestDto requestDto = createTestRequestDto();
    UpdateProfileRequest protoRequest = requestDto.toUpdateProfileRequestProto();
    UpdateProfileResponse protoResponse = UpdateProfileResponse.newBuilder()
        .setId(TEST_USER_ID)
        .build();

    when(userBlockingStub.updateProfile(eq(protoRequest)))
        .thenReturn(protoResponse);

    // When
    UpdateProfileResponseDto response = updateProfileService.updateProfile(requestDto);

    // Then
    assertNotNull(response);
    assertEquals(TEST_USER_ID, response.getId());
    verify(userBlockingStub).updateProfile(eq(protoRequest));
  }

  @Test
  @DisplayName("gRPC 서버 내부 에러 발생 시 예외 처리")
  void updateProfile_WhenGrpcInternalError() {
    // Given
    UpdateProfileRequestDto requestDto = createTestRequestDto();
    when(userBlockingStub.updateProfile(any(UpdateProfileRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.INTERNAL));

    // When & Then
    Exception exception = assertThrows(RuntimeException.class,
        () -> updateProfileService.updateProfile(requestDto));
    assertTrue(exception.getMessage().contains("Error occurred in UpdateUserService"));
  }

  @Test
  @DisplayName("잘못된 요청 데이터로 인한 실패")
  void updateProfile_WhenInvalidRequest() {
    // Given
    UpdateProfileRequestDto requestDto = createTestRequestDto();
    when(userBlockingStub.updateProfile(any(UpdateProfileRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.INVALID_ARGUMENT));

    // When & Then
    assertThrows(RuntimeException.class,
        () -> updateProfileService.updateProfile(requestDto));
  }

  @Test
  @DisplayName("사용자를 찾을 수 없는 경우 예외 처리")
  void updateProfile_WhenUserNotFound() {
    // Given
    UpdateProfileRequestDto requestDto = createTestRequestDto();
    when(userBlockingStub.updateProfile(any(UpdateProfileRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

    // When & Then
    assertThrows(RuntimeException.class,
        () -> updateProfileService.updateProfile(requestDto));
  }

  private UpdateProfileRequestDto createTestRequestDto() {
    return UpdateProfileRequestDto.builder()
        .userId(TEST_USER_ID)
        .name("John Doe")
        .bio("Software Engineer")
        .institutionId("test-institution-id")
        .enlistmentDate("2023-01-01")
        .dischargeDate("2025-01-01")
        .build();
  }
}
