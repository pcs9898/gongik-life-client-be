package org.example.gongiklifeclientbeuserservice.service;

import com.gongik.authservice.domain.service.AuthServiceGrpc;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

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


  public void saveEmailVerificationCode(String email, String code) {
    String key = VERIFICATION_CODE + email;
    redisTemplate.opsForValue()
        .set(key, code, EMAIL_VERIFICATION_CODE_EXPIRATION_MINUTES, TimeUnit.MINUTES);
  }

  public boolean isEmailVerified(String email) {
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
