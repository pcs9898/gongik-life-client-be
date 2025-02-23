package org.example.gongiklifeclientbeuserservice.service;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.UserProfileInstitution;
import com.gongik.userService.domain.service.UserServiceOuterClass.UserProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.UserProfileResponse;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbeuserservice.entity.User;
import org.example.gongiklifeclientbeuserservice.entity.UserProfile;
import org.example.gongiklifeclientbeuserservice.repository.UserProfileRepository;
import org.example.gongiklifeclientbeuserservice.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

  private final UserRepository userRepository;
  private final UserProfileRepository userProfileRepository;

  @GrpcClient("gongik-life-client-be-institution-service")
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionServiceBlockingStub;

  public UserProfileResponse userProfile(UserProfileRequest request) {
    String userId = request.getUserId();
    User user = findUserById(userId);
    UserProfile userProfile = findUserProfileByUser(user);

    return buildUserProfileResponse(user, userProfile);
  }

  private User findUserById(String userId) {
    return userRepository.findById(UUID.fromString(userId))
        .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
  }

  private UserProfile findUserProfileByUser(User user) {
    return userProfileRepository.findByUser(user)
        .orElseThrow(() -> new RuntimeException("User profile not found with ID: " + user.getId()));
  }

  private UserProfileResponse buildUserProfileResponse(User user, UserProfile userProfile) {
    UserProfileResponse.Builder responseBuilder = createBaseProfileBuilder(user, userProfile);
    addOptionalFields(responseBuilder, userProfile);
    addInstitutionInfo(responseBuilder, userProfile);

    return responseBuilder.build();
  }

  private UserProfileResponse.Builder createBaseProfileBuilder(User user, UserProfile userProfile) {
    return UserProfileResponse.newBuilder()
        .setId(user.getId().toString())
        .setName(userProfile.getName());
  }

  private void addOptionalFields(UserProfileResponse.Builder builder, UserProfile userProfile) {
    Optional.ofNullable(userProfile.getBio())
        .filter(bio -> !bio.isEmpty())
        .ifPresent(builder::setBio);

    Optional.ofNullable(userProfile.getEnlistmentDate())
        .map(Object::toString)
        .ifPresent(builder::setEnlistmentDate);

    Optional.ofNullable(userProfile.getDischargeDate())
        .map(Object::toString)
        .ifPresent(builder::setDischargeDate);
  }

  private void addInstitutionInfo(UserProfileResponse.Builder builder, UserProfile userProfile) {
    Optional.ofNullable(userProfile.getInstitutionId())
        .ifPresent(institutionId -> {
          try {
            GetInstitutionNameResponse institutionResponse = getInstitutionName(institutionId);
            builder.setInstitution(createInstitutionBuilder(institutionId, institutionResponse));
            log.info("Institution name retrieved for institution ID: {}", institutionId);
          } catch (Exception e) {
            log.error("Error occurred while getting institution name: ", e);
            throw e;
          }
        });
  }

  private GetInstitutionNameResponse getInstitutionName(UUID institutionId) {
    return institutionServiceBlockingStub.getInstitutionName(
        GetInstitutionNameRequest.newBuilder()
            .setId(institutionId.toString())
            .build()
    );
  }

  private UserProfileInstitution createInstitutionBuilder(UUID institutionId,
      GetInstitutionNameResponse response) {
    return UserProfileInstitution.newBuilder()
        .setId(institutionId.toString())
        .setName(response.getName())
        .build();
  }
}
