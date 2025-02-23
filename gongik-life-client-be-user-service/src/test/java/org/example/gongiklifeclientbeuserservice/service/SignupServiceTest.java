package org.example.gongiklifeclientbeuserservice.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gongik.authservice.domain.service.AuthServiceGrpc;
import com.gongik.authservice.domain.service.AuthServiceOuterClass.GenerateTokenResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.SignUpRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.SignUpResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.UUID;
import org.example.gongiklifeclientbeuserservice.entity.User;
import org.example.gongiklifeclientbeuserservice.entity.UserAuth;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SignupServiceTest {

  private static final String TEST_NAME = "Test User";
  private static final String TEST_EMAIL = "test@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String TEST_INSTITUTION_ID = "550e8400-e29b-41d4-a716-446655440000";

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserAuthRepository userAuthRepository;
  @Mock
  private UserProfileRepository userProfileRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private CommonUserService commonUserService;
  @Mock
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionServiceBlockingStub;
  @Mock
  private AuthServiceGrpc.AuthServiceBlockingStub authServiceBlockingStub;
  @InjectMocks
  private SignupService signupService;

  @BeforeEach
  void setUp() {
    // stub 직접 주입
    ReflectionTestUtils.setField(signupService, "institutionServiceBlockingStub",
        institutionServiceBlockingStub);
    ReflectionTestUtils.setField(signupService, "authServiceBlockingStub", authServiceBlockingStub);

    // 기본 응답 설정
    when(institutionServiceBlockingStub.getInstitutionName(any()))
        .thenReturn(GetInstitutionNameResponse.newBuilder()
            .setName("Test Institution")
            .build());

    when(authServiceBlockingStub.generateToken(any()))
        .thenReturn(GenerateTokenResponse.newBuilder()
            .setAccessToken("test-access-token")
            .setRefreshToken("test-refresh-token")
            .setAccessTokenExpiresAt("2024-02-24T00:00:00Z")
            .build());

    lenient().when(institutionServiceBlockingStub.getInstitutionName(any()))
        .thenReturn(GetInstitutionNameResponse.newBuilder()
            .setName("Test Institution")
            .build());

    lenient().when(authServiceBlockingStub.generateToken(any()))
        .thenReturn(createMockTokenResponse());
  }

  @Test
  @DisplayName("회원가입 성공 테스트")
  void signUp_Success() {
    // Given
    SignUpRequest request = createSignUpRequest();
    User mockUser = createMockUser();
    UserProfile mockProfile = createMockUserProfile(mockUser);

    // Mock 설정
    when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
    when(commonUserService.isEmailVerified(TEST_EMAIL)).thenReturn(true);
    when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(mockUser);
    when(userProfileRepository.save(any(UserProfile.class))).thenReturn(mockProfile);
    GetInstitutionNameRequest institutionNameRequest = GetInstitutionNameRequest.newBuilder()
        .setId(TEST_INSTITUTION_ID)
        .build();

    when(institutionServiceBlockingStub.getInstitutionName(eq((institutionNameRequest))))
        .thenReturn(GetInstitutionNameResponse.newBuilder()
            .setName("Test Institution")
            .build());

    // When
    SignUpResponse response = signupService.signUp(request);

    // Then
    assertNotNull(response);
    assertEquals(mockUser.getId().toString(), response.getUser().getId());
    assertEquals(TEST_EMAIL, response.getUser().getEmail());
    assertEquals(TEST_NAME, response.getUser().getName());

    verify(userRepository).save(any(User.class));
    verify(userAuthRepository).save(any(UserAuth.class));
    verify(userProfileRepository).save(any(UserProfile.class));
  }


  @Test
  @DisplayName("이미 존재하는 이메일로 가입 시도 시 실패")
  void signUp_WithExistingEmail_ThrowsException() {
    // Given
    SignUpRequest request = createSignUpRequest();
    when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

    // When & Then
    StatusRuntimeException exception = assertThrows(
        StatusRuntimeException.class,
        () -> signupService.signUp(request)
    );
    assertEquals(Status.Code.ALREADY_EXISTS, exception.getStatus().getCode());
  }

  @Test
  @DisplayName("이메일 미인증 상태로 가입 시도 시 실패")
  void signUp_WithUnverifiedEmail_ThrowsException() {
    // Given
    SignUpRequest request = createSignUpRequest();
    when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
    when(commonUserService.isEmailVerified(TEST_EMAIL)).thenReturn(false);

    // When & Then
    StatusRuntimeException exception = assertThrows(
        StatusRuntimeException.class,
        () -> signupService.signUp(request)
    );
    assertEquals(Status.Code.PERMISSION_DENIED, exception.getStatus().getCode());
  }

  private SignUpRequest createSignUpRequest() {
    return SignUpRequest.newBuilder()
        .setEmail(TEST_EMAIL)
        .setPassword(TEST_PASSWORD)
        .setConfirmPassword(TEST_PASSWORD)
        .setName(TEST_NAME)
        .setInstitutionId(TEST_INSTITUTION_ID)
        .build();
  }

  private User createMockUser() {
    return User.builder()
        .id(UUID.randomUUID())
        .email(TEST_EMAIL)
        .build();
  }

  private UserProfile createMockUserProfile(User user) {
    return UserProfile.builder()
        .user(user)
        .name(TEST_NAME)
        .institutionId(UUID.fromString(TEST_INSTITUTION_ID))
        .build();
  }

  private GenerateTokenResponse createMockTokenResponse() {
    return GenerateTokenResponse.newBuilder()
        .setAccessToken("test-access-token")
        .setRefreshToken("test-refresh-token")
        .setAccessTokenExpiresAt("2024-02-24T00:00:00Z")
        .build();
  }
}