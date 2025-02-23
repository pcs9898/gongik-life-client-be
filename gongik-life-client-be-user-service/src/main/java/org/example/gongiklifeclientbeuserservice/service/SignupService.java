package org.example.gongiklifeclientbeuserservice.service;

import com.gongik.authservice.domain.service.AuthServiceGrpc;
import com.gongik.authservice.domain.service.AuthServiceOuterClass.GenerateTokenRequest;
import com.gongik.authservice.domain.service.AuthServiceOuterClass.GenerateTokenResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass;
import com.gongik.userService.domain.service.UserServiceOuterClass.SignUpRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.SignUpResponse;
import io.grpc.Status;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbeuserservice.entity.User;
import org.example.gongiklifeclientbeuserservice.entity.UserAuth;
import org.example.gongiklifeclientbeuserservice.entity.UserProfile;
import org.example.gongiklifeclientbeuserservice.repository.UserAuthRepository;
import org.example.gongiklifeclientbeuserservice.repository.UserProfileRepository;
import org.example.gongiklifeclientbeuserservice.repository.UserRepository;
import org.example.gongiklifeclientbeuserservice.util.TimestampConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignupService {

  private final CommonUserService commonUserService;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final UserProfileRepository userProfileRepository;
  private final UserAuthRepository userAuthRepository;

  @GrpcClient("gongik-life-client-be-institution-service")
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionServiceBlockingStub;
  @GrpcClient("gongik-life-client-be-auth-service")
  private AuthServiceGrpc.AuthServiceBlockingStub authServiceBlockingStub;

  @Transactional()
  public SignUpResponse signUp(SignUpRequest request) {
    log.info("Starting signUp process for email: {}", request.getEmail());

    validateSignUpRequest(request);

    User newUser = createUser(request);
    createUserAuth(newUser, request.getPassword());
    UserProfile userProfile = createUserProfile(newUser, request);

    String institutionName = getInstitutionName(request.getInstitutionId());
    GenerateTokenResponse tokenResponse = generateUserTokens(newUser.getId());

    return buildSignUpResponse(newUser, userProfile, institutionName, request.getInstitutionId(),
        tokenResponse);
  }

  private void validateSignUpRequest(SignUpRequest request) {
    validateEmailNotExists(request.getEmail());
    validateEmailVerified(request.getEmail());
    validatePasswordMatch(request.getPassword(), request.getConfirmPassword());
  }

  private void validateEmailNotExists(String email) {
    if (userRepository.existsByEmail(email)) {
      log.error("Email is already registered: {}", email);
      throw Status.ALREADY_EXISTS
          .withDescription("Email is already registered.")
          .asRuntimeException();
    }
  }

  private void validateEmailVerified(String email) {
    if (!commonUserService.isEmailVerified(email)) {
      log.error("Email is not verified: {}", email);
      throw Status.PERMISSION_DENIED
          .withDescription("Email is not verified.")
          .asRuntimeException();
    }
  }

  private void validatePasswordMatch(String password, String confirmPassword) {
    if (!password.equals(confirmPassword)) {
      log.error("Password and password confirm do not match");
      throw Status.INVALID_ARGUMENT
          .withDescription("Password and password confirm are not same.")
          .asRuntimeException();
    }
  }

  private User createUser(SignUpRequest request) {
    User newUser = User.builder()
        .email(request.getEmail())
        .build();
    return userRepository.save(newUser);
  }

  private void createUserAuth(User user, String password) {
    UserAuth newUserAuth = UserAuth.builder()
        .user(user)
        .authTypeId(1)
        .passwordHash(passwordEncoder.encode(password))
        .build();
    userAuthRepository.save(newUserAuth);
  }

  private UserProfile createUserProfile(User user, SignUpRequest request) {
    UserProfile newUserProfile = UserProfile.builder()
        .user(user)
        .name(request.getName())
        .institutionId(request.getInstitutionId().isEmpty() ? null :
            UUID.fromString(request.getInstitutionId()))
        .bio(request.getBio().isEmpty() ? null : request.getBio())
        .enlistmentDate(request.getEnlistmentDate().isEmpty() ? null :
            TimestampConverter.convertStringToDate(request.getEnlistmentDate()))
        .dischargeDate(request.getDischargeDate().isEmpty() ? null :
            TimestampConverter.convertStringToDate(request.getDischargeDate()))
        .build();
    return userProfileRepository.save(newUserProfile);
  }

  private String getInstitutionName(String institutionId) {
    if (institutionId.isEmpty()) {
      return null;
    }
    try {
      GetInstitutionNameResponse response = institutionServiceBlockingStub.getInstitutionName(
          GetInstitutionNameRequest.newBuilder().setId(institutionId).build());
      return response.getName();
    } catch (Exception e) {
      log.error("Error getting institution name: ", e);
      throw e;
    }
  }

  private GenerateTokenResponse generateUserTokens(UUID userId) {
    try {
      GenerateTokenResponse response = authServiceBlockingStub.generateToken(
          GenerateTokenRequest.newBuilder()
              .setUserId(userId.toString())
              .build());
      if (response == null) {
        throw new RuntimeException("Failed to generate token: response is null");
      }
      return response;
    } catch (Exception e) {
      log.error("Error generating tokens: ", e);
      throw e;
    }
  }

  private SignUpResponse buildSignUpResponse(
      User user,
      UserProfile userProfile,
      String institutionName,
      String institutionId,
      GenerateTokenResponse tokenResponse) {

    UserServiceOuterClass.SignUpUser.Builder userBuilder = UserServiceOuterClass.SignUpUser.newBuilder()
        .setId(user.getId().toString())
        .setEmail(user.getEmail())
        .setName(userProfile.getName());

    Optional.ofNullable(userProfile.getBio())
        .filter(bio -> !bio.isEmpty())
        .ifPresent(userBuilder::setBio);

    Optional.ofNullable(userProfile.getEnlistmentDate())
        .map(Object::toString)
        .ifPresent(userBuilder::setEnlistmentDate);

    Optional.ofNullable(userProfile.getDischargeDate())
        .map(Object::toString)
        .ifPresent(userBuilder::setDischargeDate);

    if (institutionName != null) {
      userBuilder.setInstitution(UserServiceOuterClass.SignUpInstitution.newBuilder()
          .setId(institutionId)
          .setName(institutionName)
          .build());
    }

    return SignUpResponse.newBuilder()
        .setUser(userBuilder.build())
        .setRefreshToken(tokenResponse.getRefreshToken())
        .setAccessToken(tokenResponse.getAccessToken())
        .setAccessTokenExpiresAt(tokenResponse.getAccessTokenExpiresAt())
        .build();
  }
}