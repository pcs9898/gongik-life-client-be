package org.example.gongiklifeclientbeuserservice.service;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.UpdateProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.UpdateProfileResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.example.gongiklifeclientbeuserservice.entity.User;
import org.example.gongiklifeclientbeuserservice.entity.UserProfile;
import org.example.gongiklifeclientbeuserservice.repository.UserAuthRepository;
import org.example.gongiklifeclientbeuserservice.repository.UserProfileRepository;
import org.example.gongiklifeclientbeuserservice.repository.UserRepository;
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

  private static final String TEST_USER_ID = "123e4567-e89b-12d3-a456-426614174000";
  private static final String TEST_INSTITUTION_ID = "123e4567-e89b-12d3-a456-426614174001";

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserProfileRepository userProfileRepository;

  @Mock
  private UserAuthRepository userAuthRepository;

  @Mock
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionServiceBlockingStub;

  @InjectMocks
  private UpdateProfileService updateProfileService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(updateProfileService, "institutionServiceBlockingStub",
        institutionServiceBlockingStub);
  }

  @Test
  @DisplayName("프로필 업데이트 성공 - 모든 필드 포함")
  void updateProfile_Success_WithAllFields() {
    // Given
    User user = createTestUser();
    UserProfile userProfile = createTestUserProfile();
    UpdateProfileRequest request = createTestRequest();
    GetInstitutionNameResponse institutionResponse = GetInstitutionNameResponse.newBuilder()
        .setName("Test Institution")
        .build();

    when(userRepository.findById(UUID.fromString(TEST_USER_ID))).thenReturn(Optional.of(user));
    when(userProfileRepository.findByUser(user)).thenReturn(Optional.of(userProfile));
    when(institutionServiceBlockingStub.getInstitutionName(any())).thenReturn(institutionResponse);

    // When
    UpdateProfileResponse response = updateProfileService.updateProfile(request);

    // Then
    verify(userProfileRepository).save(any(UserProfile.class));
    assertAll(
        () -> assertEquals(TEST_USER_ID, response.getId()),
        () -> assertEquals("John Doe", response.getName()),
        () -> assertEquals("Software Engineer", response.getBio()),
        () -> assertEquals("Sun Jan 01 00:00:00 KST 2023", response.getEnlistmentDate()),
        () -> assertEquals("Wed Jan 01 00:00:00 KST 2025", response.getDischargeDate()),
        () -> assertEquals(TEST_INSTITUTION_ID, response.getInstitution().getId()),
        () -> assertEquals("Test Institution", response.getInstitution().getName())
    );
  }

  @Test
  @DisplayName("사용자를 찾을 수 없는 경우 예외 발생")
  void updateProfile_WhenUserNotFound() {
    // Given
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    // When & Then
    assertThrows(RuntimeException.class,
        () -> updateProfileService.updateProfile(createTestRequest()));
  }

  @Test
  @DisplayName("기관 정보 없이 날짜 정보만 제공된 경우 예외 발생")
  void updateProfile_WhenInstitutionIdProvidedWithoutDates() {
    // Given
    UpdateProfileRequest request = UpdateProfileRequest.newBuilder()
        .setUserId(TEST_USER_ID)
        .setInstitutionId(TEST_INSTITUTION_ID)
        .build();

    User user = createTestUser();
    UserProfile userProfile = createTestUserProfile();
    userProfile.setEnlistmentDate(null);
    userProfile.setDischargeDate(null);

    when(userRepository.findById(UUID.fromString(TEST_USER_ID))).thenReturn(Optional.of(user));
    when(userProfileRepository.findByUser(user)).thenReturn(Optional.of(userProfile));

    // When & Then
    StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
        () -> updateProfileService.updateProfile(request));
    assertEquals(Status.Code.INVALID_ARGUMENT, exception.getStatus().getCode());
  }

  private User createTestUser() {
    User user = new User();
    user.setId(UUID.fromString(TEST_USER_ID));
    user.setEmail("test@example.com");
    return user;
  }

  private UserProfile createTestUserProfile() {
    UserProfile userProfile = new UserProfile();
    userProfile.setName("John Doe");
    userProfile.setBio("Software Engineer");
    userProfile.setEnlistmentDate(Date.from(LocalDate.of(2023, 1, 1)
        .atStartOfDay(ZoneId.systemDefault()).toInstant()));
    userProfile.setDischargeDate(Date.from(LocalDate.of(2025, 1, 1)
        .atStartOfDay(ZoneId.systemDefault()).toInstant()));
    userProfile.setInstitutionId(UUID.fromString(TEST_INSTITUTION_ID));
    return userProfile;
  }

  private UpdateProfileRequest createTestRequest() {
    return UpdateProfileRequest.newBuilder()
        .setUserId(TEST_USER_ID)
        .setName("John Doe")
        .setBio("Software Engineer")
        .setEnlistmentDate("2023-01-01")
        .setDischargeDate("2025-01-01")
        .setInstitutionId(TEST_INSTITUTION_ID)
        .build();
  }
}
