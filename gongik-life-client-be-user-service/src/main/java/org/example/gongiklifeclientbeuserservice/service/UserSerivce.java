package org.example.gongiklifeclientbeuserservice.service;

import com.gongik.authservice.domain.service.AuthServiceGrpc;
import com.gongik.authservice.domain.service.AuthServiceOuterClass.GenerateTokenRequest;
import com.gongik.authservice.domain.service.AuthServiceOuterClass.GenerateTokenResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass;
import com.gongik.userService.domain.service.UserServiceOuterClass.CheckUserInstitutionRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.CheckUserInstitutionResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.FindByEmailForAuthRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.FindByEmailForAuthResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdsRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdsResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.HasInstitutionRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.HasInstitutionResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileInstitution;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.SignUpRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.SignUpResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.UpdateProfileInstitution;
import com.gongik.userService.domain.service.UserServiceOuterClass.UpdateProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.UpdateProfileResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.UserProfileInstitution;
import com.gongik.userService.domain.service.UserServiceOuterClass.UserProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.UserProfileResponse;
import io.grpc.Status;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbeuserservice.entity.User;
import org.example.gongiklifeclientbeuserservice.entity.UserAuth;
import org.example.gongiklifeclientbeuserservice.entity.UserProfile;
import org.example.gongiklifeclientbeuserservice.repository.AuthTypeRepository;
import org.example.gongiklifeclientbeuserservice.repository.UserAuthRepository;
import org.example.gongiklifeclientbeuserservice.repository.UserProfileRepository;
import org.example.gongiklifeclientbeuserservice.repository.UserRepository;
import org.example.gongiklifeclientbeuserservice.util.TimestampConverter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserSerivce {

  private static final long EMAIL_VERIFICATION_CODE_EXPIRATION_MINUTES = 6;
  private static final long EMAIL_VERIFICATION_CODE_RESEND_WAIT_MINUTES = 1;
  private static final long VERIFIED_EMAIL_EXPIRATION_MINUTES = 30;


  private static final String VERIFIED_EMAIL = "verified:email:";
  private static final String VERIFICATION_CODE = "verification:code:";
  private final RedisTemplate<String, String> redisTemplate;
  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;
  private final UserAuthRepository userAuthRepository;
  private final UserProfileRepository userProfileRepository;
  private final AuthTypeRepository authTypeRepository;
  @GrpcClient("gongik-life-client-be-auth-service")
  private AuthServiceGrpc.AuthServiceBlockingStub authServiceBlockingStub;
  @GrpcClient("gongik-life-client-be-institution-service")
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionServiceBlockingStub;


  @Transactional()
  public SignUpResponse signUp(SignUpRequest request) {
    log.info("Starting signUp process for email: {}", request.getEmail());

    try {
      // 이메일 중복 확인
      boolean emailExists = userRepository.existsByEmail(request.getEmail());
      if (emailExists) {
        log.error("Email is already registered: {}", request.getEmail());
        throw Status.ALREADY_EXISTS
            .withDescription("Email is already registered.")
            .asRuntimeException();
      }

      boolean isVerified = isEmailVerified(request.getEmail());
      if (!isVerified) {
        log.error("Email is not verified: {}", request.getEmail());
        throw Status.PERMISSION_DENIED
            .withDescription("Email is not verified.")
            .asRuntimeException();
      }

      if (!request.getPassword().equals(request.getConfirmPassword())) {
        log.error("Password and password confirm do not match for email: {}", request.getEmail());
        throw Status.INVALID_ARGUMENT
            .withDescription("Password and password confirm are not same.")
            .asRuntimeException();
      }

      String hashedPassword = passwordEncoder.encode(request.getPassword());

      // 사용자 생성
      User newUser = User.builder()
          .email(request.getEmail())
          .build();
      userRepository.save(newUser);
      log.info("New user created with ID: {}", newUser.getId());

      UserAuth newUserAuth = UserAuth.builder()
          .user(newUser)
          .authTypeId(1)
          .passwordHash(hashedPassword)
          .build();
      userAuthRepository.save(newUserAuth);
      log.info("New user auth created for user ID: {}", newUser.getId());

      UserProfile newUserProfile = UserProfile.builder()
          .user(newUser)
          .name(request.getName())
          .institutionId(request.getInstitutionId().isEmpty() ? null
              : UUID.fromString(request.getInstitutionId()))
          .bio(request.getBio().isEmpty() ? null : request.getBio())
          .enlistmentDate(request.getEnlistmentDate().isEmpty() ? null
              : TimestampConverter.convertStringToDate(request.getEnlistmentDate()))
          .dischargeDate(request.getDischargeDate().isEmpty() ? null
              : TimestampConverter.convertStringToDate(request.getDischargeDate()))
          .build();
      userProfileRepository.save(newUserProfile);
      log.info("New user profile created for user ID: {}", newUser.getId());

      GetInstitutionNameResponse getInstitutionNameRequest = null;
      if (!request.getInstitutionId().isEmpty()) {
        try {
          getInstitutionNameRequest = institutionServiceBlockingStub.getInstitutionName(
              GetInstitutionNameRequest.newBuilder().setId(request.getInstitutionId()).build());
          log.info("Institution name retrieved for institution ID: {}", request.getInstitutionId());
        } catch (Exception e) {
          log.error("Error occurred while getting institution name: ", e);
          throw e;
        }
      }

      GenerateTokenResponse generateTokenResponse;
      try {
        generateTokenResponse = authServiceBlockingStub.generateToken(
            GenerateTokenRequest.newBuilder()
                .setUserId(newUser.getId().toString())
                .build());
        if (generateTokenResponse == null) {
          log.error("generateTokenResponse is null");
          throw new RuntimeException("Failed to generate token: response is null");
        }
        log.info("generateTokenResponse: {}", generateTokenResponse);
      } catch (Exception e) {
        log.error("Error occurred while generating token: ", e);
        throw e;
      }

      UserServiceOuterClass.SignUpUser.Builder signUpUserBuilder = UserServiceOuterClass.SignUpUser.newBuilder()
          .setId(newUser.getId().toString())
          .setEmail(newUser.getEmail())
          .setName(newUserProfile.getName());

      if (newUserProfile.getBio() != null && !newUserProfile.getBio().isEmpty()) {
        signUpUserBuilder.setBio(newUserProfile.getBio());
      }

      if (newUserProfile.getEnlistmentDate() != null) {
        signUpUserBuilder.setEnlistmentDate(newUserProfile.getEnlistmentDate().toString());
      }

      if (newUserProfile.getDischargeDate() != null) {
        signUpUserBuilder.setDischargeDate(newUserProfile.getDischargeDate().toString());
      }

      if (getInstitutionNameRequest != null) {
        signUpUserBuilder.setInstitution(UserServiceOuterClass.SignUpInstitution.newBuilder()
            .setId(request.getInstitutionId())
            .setName(getInstitutionNameRequest.getName())
            .build());
      }

      SignUpResponse response = SignUpResponse.newBuilder()
          .setUser(signUpUserBuilder.build())
          .setRefreshToken(generateTokenResponse.getRefreshToken())
          .setAccessToken(generateTokenResponse.getAccessToken())
          .setAccessTokenExpiresAt(generateTokenResponse.getAccessTokenExpiresAt())
          .build();

      log.info("SignUpResponse: {}", response);

      return response;
    } catch (Exception e) {
      log.error("signUp error: ", e);
      throw e;
    }
  }


  private void saveEmailVerificationCode(String email, String code) {
    String key = VERIFICATION_CODE + email;
    redisTemplate.opsForValue()
        .set(key, code, EMAIL_VERIFICATION_CODE_EXPIRATION_MINUTES, TimeUnit.MINUTES);
  }

  private boolean isEmailVerified(String email) {
    String verifiedKey = VERIFIED_EMAIL + email;
    String isVerified = redisTemplate.opsForValue().get(verifiedKey);

    return "true".equals(isVerified);
  }

  public FindByEmailForAuthResponse findByEmailForAuth(FindByEmailForAuthRequest request) {
    try {
      String email = request.getEmail();
      User user = userRepository.findByEmail(email)
          .orElseThrow(() ->
              Status.NOT_FOUND
                  .withDescription("User not found with email: " + email)
                  .asRuntimeException()

          );

      UserAuth userAuth = userAuthRepository.findByUser(user)
          .orElseThrow(() ->
              Status.NOT_FOUND
                  .withDescription("User auth not found with email: " + email)
                  .asRuntimeException()

          );

      UserProfile userProfile = userProfileRepository.findByUser(user)
          .orElseThrow(() ->
              Status.NOT_FOUND
                  .withDescription("User profile not found with email: " + email)
                  .asRuntimeException()
          );

      UserServiceOuterClass.FindByEmailForAuthResponse.Builder findByEmailForAuthResponseBuilder = UserServiceOuterClass.FindByEmailForAuthResponse.newBuilder()
          .setId(user.getId().toString())
          .setEmail(user.getEmail())
          .setPassword(userAuth.getPasswordHash())
          .setName(userProfile.getName())
          .setEnlistmentDate(userProfile.getEnlistmentDate() == null ? ""
              : userProfile.getEnlistmentDate().toString())
          .setDischargeDate(userProfile.getDischargeDate() == null ? ""
              : userProfile.getDischargeDate().toString());

      if (userProfile.getBio() != null && !userProfile.getBio().isEmpty()) {
        findByEmailForAuthResponseBuilder.setBio(userProfile.getBio());
      }

      GetInstitutionNameResponse getInstitutionNameRequest = null;
      if (userProfile.getInstitutionId() != null) {
        try {
          getInstitutionNameRequest = institutionServiceBlockingStub.getInstitutionName(
              GetInstitutionNameRequest.newBuilder()
                  .setId(userProfile.getInstitutionId().toString())
                  .build());
          log.info("Institution name retrieved for institution ID: {}",
              userProfile.getInstitutionId().toString());
        } catch (Exception e) {
          log.error("Error occurred while getting institution name: ", e);
          throw e;
        }
      }

      if (getInstitutionNameRequest != null) {
        findByEmailForAuthResponseBuilder.setInstitution(
            UserServiceOuterClass.InstitutionForAuth.newBuilder()
                .setId(userProfile.getInstitutionId().toString())
                .setName(getInstitutionNameRequest.getName()));
      }

      return findByEmailForAuthResponseBuilder.build();
    } catch (Exception e) {
      log.error("Error in findByEmailForAuth: ", e);
      throw Status.INTERNAL
          .withDescription(e.getMessage())
          .withCause(e)
          .asRuntimeException();
    }
  }

  public MyProfileResponse myProfile(MyProfileRequest request) {
    String userId = request.getUserId();

    User user = userRepository.findById(UUID.fromString(userId))
        .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

    UserProfile userProfile = userProfileRepository.findByUser(user)
        .orElseThrow(() -> new RuntimeException("User profile not found with ID: " + userId));

    MyProfileResponse.Builder myProfileResponseBuilder = MyProfileResponse.newBuilder()
        .setId(user.getId().toString())
        .setEmail(user.getEmail())
        .setName(userProfile.getName());

    if (!userProfile.getBio().isEmpty()) {
      myProfileResponseBuilder.setBio(userProfile.getBio());
    }

    if (userProfile.getEnlistmentDate() != null) {
      myProfileResponseBuilder.setEnlistmentDate(userProfile.getEnlistmentDate().toString());
    }

    if (userProfile.getDischargeDate() != null) {
      myProfileResponseBuilder.setDischargeDate(userProfile.getDischargeDate().toString());
    }

    GetInstitutionNameResponse getInstitutionNameRequest = null;
    if (userProfile.getInstitutionId() != null) {
      try {
        getInstitutionNameRequest = institutionServiceBlockingStub.getInstitutionName(
            GetInstitutionNameRequest.newBuilder().setId(userProfile.getInstitutionId().toString())
                .build());
        log.info("Institution name retrieved for institution ID: {}",
            userProfile.getInstitutionId().toString());
      } catch (Exception e) {
        log.error("Error occurred while getting institution name: ", e);
        throw e;
      }
    }

    if (getInstitutionNameRequest != null) {
      myProfileResponseBuilder.setInstitution(
          MyProfileInstitution.newBuilder()
              .setId(userProfile.getInstitutionId().toString())
              .setName(getInstitutionNameRequest.getName()));
    }

    return myProfileResponseBuilder.build();
  }

  public UserProfileResponse userProfile(UserProfileRequest request) {
    String userId = request.getUserId();

    User user = userRepository.findById(UUID.fromString(userId))
        .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

    UserProfile userProfile = userProfileRepository.findByUser(user)
        .orElseThrow(() -> new RuntimeException("User profile not found with ID: " + userId));

    UserProfileResponse.Builder userProfileResponseBuilder = UserProfileResponse.newBuilder()
        .setId(user.getId().toString())
        .setName(userProfile.getName());

    if (!userProfile.getBio().isEmpty()) {
      userProfileResponseBuilder.setBio(userProfile.getBio());
    }

    if (userProfile.getEnlistmentDate() != null) {
      userProfileResponseBuilder.setEnlistmentDate(userProfile.getEnlistmentDate().toString());
    }

    if (userProfile.getDischargeDate() != null) {
      userProfileResponseBuilder.setDischargeDate(userProfile.getDischargeDate().toString());
    }

    GetInstitutionNameResponse getInstitutionNameRequest = null;
    if (userProfile.getInstitutionId() != null) {
      try {
        getInstitutionNameRequest = institutionServiceBlockingStub.getInstitutionName(
            GetInstitutionNameRequest.newBuilder().setId(userProfile.getInstitutionId().toString())
                .build());
        log.info("Institution name retrieved for institution ID: {}",
            userProfile.getInstitutionId().toString());
      } catch (Exception e) {
        log.error("Error occurred while getting institution name: ", e);
        throw e;
      }
    }

    if (getInstitutionNameRequest != null) {
      userProfileResponseBuilder.setInstitution(
          UserProfileInstitution.newBuilder()
              .setId(userProfile.getInstitutionId().toString())
              .setName(getInstitutionNameRequest.getName()));
    }

    return userProfileResponseBuilder.build();
  }

  public UpdateProfileResponse updateProfile(UpdateProfileRequest request) {

    User user = userRepository.findById(UUID.fromString(request.getUserId()))
        .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

    UserProfile userProfile = userProfileRepository.findByUser(user)
        .orElseThrow(
            () -> new RuntimeException("User profile not found with ID: " + request.getUserId()));

    if (request.hasInstitutionId()) {
      boolean hasUserProfileDates = userProfile.getDischargeDate() != null &&
          userProfile.getEnlistmentDate() != null;
      boolean hasRequestDates = request.hasEnlistmentDate() &&
          request.hasDischargeDate();

      if (!hasUserProfileDates && !hasRequestDates) {
        throw Status.INVALID_ARGUMENT
            .withDescription(
                "Institution ID, Enlistment Date, and Discharge Date must all be provided.")
            .asRuntimeException();
      }
    }

    if (request.hasName()) {
      userProfile.setName(request.getName());
      if (request.getName().isEmpty()) {
        throw Status.INVALID_ARGUMENT
            .withDescription("Name cannot be empty.")
            .asRuntimeException();
      } else {
        userProfile.setName(request.getName());
      }
    }
    if (request.hasBio()) {
      if (request.getBio().isEmpty()) {
        userProfile.setBio(null);
      } else {
        userProfile.setBio(request.getBio());
      }

    }
    if (request.hasEnlistmentDate()) {
      if (request.getEnlistmentDate().isEmpty()) {
        userProfile.setEnlistmentDate(null);
      } else {
        userProfile.setEnlistmentDate(
            TimestampConverter.convertStringToDate(request.getEnlistmentDate()));
      }
    }
    if (request.hasDischargeDate()) {
      if (request.getDischargeDate().isEmpty()) {
        userProfile.setDischargeDate(null);
      } else {
        userProfile.setDischargeDate(
            TimestampConverter.convertStringToDate(request.getDischargeDate()));
      }
    }
    if (request.hasInstitutionId()) {
      if (request.getInstitutionId().isEmpty()) {
        userProfile.setInstitutionId(null);
      } else {
        userProfile.setInstitutionId(UUID.fromString(request.getInstitutionId()));
      }
    }

    userProfileRepository.save(userProfile);

    UpdateProfileResponse.Builder responseBuilder = UpdateProfileResponse.newBuilder()
        .setId(user.getId().toString())
        .setEmail(user.getEmail());

    if (userProfile.getName() != null) {
      responseBuilder.setName(userProfile.getName());
    }

    if (userProfile.getBio() != null && !userProfile.getBio().isEmpty()) {
      responseBuilder.setBio(userProfile.getBio());
    }

    if (userProfile.getEnlistmentDate() != null) {
      responseBuilder.setEnlistmentDate(userProfile.getEnlistmentDate().toString());
    }

    if (userProfile.getDischargeDate() != null) {
      responseBuilder.setDischargeDate(userProfile.getDischargeDate().toString());
    }

    if (userProfile.getInstitutionId() != null) {
      try {
        String institutionName = getInstitutionName(userProfile.getInstitutionId());

        responseBuilder.setInstitution(UpdateProfileInstitution.newBuilder()
            .setId(userProfile.getInstitutionId().toString())
            .setName(institutionName));
      } catch (Exception e) {
        log.error("Error occurred while getting institution name: ", e);
        throw e;
      }
    }

    return responseBuilder.build();

  }

  private String getInstitutionName(UUID institutionId) {

    GetInstitutionNameResponse response = institutionServiceBlockingStub.getInstitutionName(
        GetInstitutionNameRequest.newBuilder().setId(institutionId.toString()).build());
    return response.getName();
  }

  public CheckUserInstitutionResponse checkUserInstitution(CheckUserInstitutionRequest request) {
    String userId = request.getUserId();
    String institutionId = request.getInstitutionId();

    User user = userRepository.findById(UUID.fromString(userId))
        .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

    UserProfile userProfile = userProfileRepository.findByUser(user)
        .orElseThrow(() -> new RuntimeException("User profile not found with ID: " + userId));

    if (userProfile.getInstitutionId() == null) {
      return CheckUserInstitutionResponse.newBuilder().setUserName("").build();
    }

    if (userProfile.getInstitutionId().toString().equals(institutionId)) {
      return CheckUserInstitutionResponse.newBuilder().setUserName(userProfile.getName()).build();
    }

    return CheckUserInstitutionResponse.newBuilder().setUserName("").build();
  }

  public GetUserNameByIdResponse getUserNameById(GetUserNameByIdRequest request) {
    String userId = request.getUserId();

    User user = userRepository.findById(UUID.fromString(userId))
        .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

    UserProfile userProfile = userProfileRepository.findByUser(user)
        .orElseThrow(() -> new RuntimeException("User profile not found with ID: " + userId));

    return GetUserNameByIdResponse.newBuilder().setUserName(userProfile.getName()).build();
  }

  public GetUserNameByIdsResponse getUserNameByIds(GetUserNameByIdsRequest request) {
    GetUserNameByIdsResponse.Builder responseBuilder = GetUserNameByIdsResponse.newBuilder();

    // 최적화된 버전
    List<String> userIds = request.getUserIdsList();
    List<UUID> uuidList = userIds.stream()
        .map(UUID::fromString)
        .collect(Collectors.toList());

// 한 번의 쿼리로 모든 사용자 프로필 조회
    List<UserProfile> userProfiles = userProfileRepository.findByUserIdIn(uuidList);

// Map으로 변환하여 응답 구성
    Map<String, String> userNamesMap = userProfiles.stream()
        .collect(Collectors.toMap(
            profile -> profile.getUser().getId().toString(),
            UserProfile::getName
        ));

    return GetUserNameByIdsResponse.newBuilder()
        .putAllUsers(userNamesMap)
        .build();

  }


  public HasInstitutionResponse hasInstitution(HasInstitutionRequest request) {

    String userId = request.getUserId();

    User user = userRepository.findById(UUID.fromString(userId))
        .orElseThrow(() -> Status.NOT_FOUND.withDescription("User not found with ID: " + userId)
            .asRuntimeException());

    UserProfile userProfile = userProfileRepository.findByUser(user)
        .orElseThrow(() ->
            Status.NOT_FOUND.withDescription("User profile not found with ID: " + userId)
                .asRuntimeException());

    if (userProfile.getInstitutionId() == null) {
      throw Status.FAILED_PRECONDITION.withDescription(
              "User has no institution, if you want to get average workhours, you should have institution")
          .asRuntimeException();
    }

    return HasInstitutionResponse.newBuilder()
        .setInstitutionId(userProfile.getInstitutionId().toString()).build();
  }
}
