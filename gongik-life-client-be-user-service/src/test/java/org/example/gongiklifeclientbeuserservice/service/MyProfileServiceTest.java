package org.example.gongiklifeclientbeuserservice.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.example.gongiklifeclientbeuserservice.entity.User;
import org.example.gongiklifeclientbeuserservice.entity.UserProfile;
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
class MyProfileServiceTest {

  private static final String TEST_USER_ID = "123e4567-e89b-12d3-a456-426614174000";
  private static final String TEST_INSTITUTION_ID = "123e4567-e89b-12d3-a456-426614174001";

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserProfileRepository userProfileRepository;

  @Mock
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionServiceBlockingStub;

  @InjectMocks
  private MyProfileService myProfileService;

  @BeforeEach
  void setUp() {
    // gRPC 클라이언트 수동 주입
    ReflectionTestUtils.setField(myProfileService, "institutionServiceBlockingStub",
        institutionServiceBlockingStub);
  }

  @Test
  @DisplayName("내 프로필 조회 성공 - 모든 필드 포함")
  void myProfile_Success_WithAllFields() {
    // Given
    User user = createTestUser();
    UserProfile userProfile = createTestUserProfile();
    GetInstitutionNameResponse institutionResponse = GetInstitutionNameResponse.newBuilder()
        .setName("Test Institution")
        .build();

    when(userRepository.findById(UUID.fromString(TEST_USER_ID))).thenReturn(Optional.of(user));
    when(userProfileRepository.findByUser(user)).thenReturn(Optional.of(userProfile));
    when(institutionServiceBlockingStub.getInstitutionName(any())).thenReturn(institutionResponse);

    // When
    MyProfileResponse response = myProfileService.myProfile(
        MyProfileRequest.newBuilder().setUserId(TEST_USER_ID).build()
    );

    // Then
    assertAll(
        () -> assertEquals(TEST_USER_ID, response.getId()),
        () -> assertEquals("test@example.com", response.getEmail()),
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
  void myProfile_WhenUserNotFound() {
    // Given
    when(userRepository.findById(any())).thenReturn(Optional.empty());

    // When & Then
    assertThrows(RuntimeException.class, () ->
        myProfileService.myProfile(
            MyProfileRequest.newBuilder().setUserId(TEST_USER_ID).build()
        )
    );
  }

  @Test
  @DisplayName("프로필을 찾을 수 없는 경우 예외 발생")
  void myProfile_WhenProfileNotFound() {
    // Given
    User user = createTestUser();
    when(userRepository.findById(UUID.fromString(TEST_USER_ID))).thenReturn(Optional.of(user));
    when(userProfileRepository.findByUser(user)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(RuntimeException.class, () ->
        myProfileService.myProfile(
            MyProfileRequest.newBuilder().setUserId(TEST_USER_ID).build()
        )
    );
  }

  @Test
  @DisplayName("기관 정보 조회 중 오류 발생")
  void myProfile_WhenInstitutionServiceFails() {
    // Given
    User user = createTestUser();
    UserProfile userProfile = createTestUserProfile();

    when(userRepository.findById(UUID.fromString(TEST_USER_ID))).thenReturn(Optional.of(user));
    when(userProfileRepository.findByUser(user)).thenReturn(Optional.of(userProfile));
    when(institutionServiceBlockingStub.getInstitutionName(any())).thenThrow(
        new RuntimeException("gRPC error"));

    // When & Then
    Exception exception = assertThrows(RuntimeException.class, () ->
        myProfileService.myProfile(
            MyProfileRequest.newBuilder().setUserId(TEST_USER_ID).build()
        )
    );
    assertEquals("gRPC error", exception.getMessage());
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
    userProfile.setEnlistmentDate(
        Date.from(LocalDate.of(2023, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    userProfile.setDischargeDate(
        Date.from(LocalDate.of(2025, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    userProfile.setInstitutionId(UUID.fromString(TEST_INSTITUTION_ID));
    return userProfile;
  }
}
