package org.example.gongiklifeclientbeuserservice.service;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.UpdateProfileInstitution;
import com.gongik.userService.domain.service.UserServiceOuterClass.UpdateProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.UpdateProfileResponse;
import io.grpc.Status;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbeuserservice.entity.User;
import org.example.gongiklifeclientbeuserservice.entity.UserProfile;
import org.example.gongiklifeclientbeuserservice.repository.UserAuthRepository;
import org.example.gongiklifeclientbeuserservice.repository.UserProfileRepository;
import org.example.gongiklifeclientbeuserservice.repository.UserRepository;
import org.example.gongiklifeclientbeuserservice.util.TimestampConverter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateProfileService {

  private final UserRepository userRepository;
  private final UserAuthRepository userAuthRepository;
  private final UserProfileRepository userProfileRepository;

  @GrpcClient("gongik-life-client-be-institution-service")
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionServiceBlockingStub;

  public UpdateProfileResponse updateProfile(UpdateProfileRequest request) {
    User user = findUserById(request.getUserId());
    UserProfile userProfile = findUserProfileByUser(user);

    validateInstitutionRequest(request, userProfile);
    updateUserProfileFields(request, userProfile);
    userProfileRepository.save(userProfile);

    return buildUpdateProfileResponse(user, userProfile);
  }

  private User findUserById(String userId) {
    return userRepository.findById(UUID.fromString(userId))
        .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
  }

  private UserProfile findUserProfileByUser(User user) {
    return userProfileRepository.findByUser(user)
        .orElseThrow(() -> new RuntimeException("User profile not found with ID: " + user.getId()));
  }

  private void validateInstitutionRequest(UpdateProfileRequest request, UserProfile userProfile) {
    if (!request.hasInstitutionId()) {
      return;
    }

    boolean hasUserProfileDates = userProfile.getDischargeDate() != null &&
        userProfile.getEnlistmentDate() != null;
    boolean hasRequestDates = request.hasEnlistmentDate() && request.hasDischargeDate();

    if (!hasUserProfileDates && !hasRequestDates) {
      throw Status.INVALID_ARGUMENT
          .withDescription(
              "Institution ID, Enlistment Date, and Discharge Date must all be provided.")
          .asRuntimeException();
    }
  }

  private void updateUserProfileFields(UpdateProfileRequest request, UserProfile userProfile) {
    updateName(request, userProfile);
    updateBio(request, userProfile);
    updateEnlistmentDate(request, userProfile);
    updateDischargeDate(request, userProfile);
    updateInstitutionId(request, userProfile);
  }

  private void updateName(UpdateProfileRequest request, UserProfile userProfile) {
    if (!request.hasName()) {
      return;
    }
    if (request.getName().isEmpty()) {
      throw Status.INVALID_ARGUMENT
          .withDescription("Name cannot be empty.")
          .asRuntimeException();
    }
    userProfile.setName(request.getName());
  }

  private void updateBio(UpdateProfileRequest request, UserProfile userProfile) {
    if (request.hasBio()) {
      userProfile.setBio(request.getBio().isEmpty() ? null : request.getBio());
    }
  }

  private void updateEnlistmentDate(UpdateProfileRequest request, UserProfile userProfile) {
    if (request.hasEnlistmentDate()) {
      userProfile.setEnlistmentDate(request.getEnlistmentDate().isEmpty() ? null :
          TimestampConverter.convertStringToDate(request.getEnlistmentDate()));
    }
  }

  private void updateDischargeDate(UpdateProfileRequest request, UserProfile userProfile) {
    if (request.hasDischargeDate()) {
      userProfile.setDischargeDate(request.getDischargeDate().isEmpty() ? null :
          TimestampConverter.convertStringToDate(request.getDischargeDate()));
    }
  }

  private void updateInstitutionId(UpdateProfileRequest request, UserProfile userProfile) {
    if (request.hasInstitutionId()) {
      userProfile.setInstitutionId(request.getInstitutionId().isEmpty() ? null :
          UUID.fromString(request.getInstitutionId()));
    }
  }

  private UpdateProfileResponse buildUpdateProfileResponse(User user, UserProfile userProfile) {
    UpdateProfileResponse.Builder responseBuilder = createBaseResponseBuilder(user, userProfile);
    addOptionalFields(responseBuilder, userProfile);
    addInstitutionInfo(responseBuilder, userProfile);
    return responseBuilder.build();
  }

  private UpdateProfileResponse.Builder createBaseResponseBuilder(User user,
      UserProfile userProfile) {
    UpdateProfileResponse.Builder builder = UpdateProfileResponse.newBuilder()
        .setId(user.getId().toString())
        .setEmail(user.getEmail());

    Optional.ofNullable(userProfile.getName())
        .ifPresent(builder::setName);

    return builder;
  }

  private void addOptionalFields(UpdateProfileResponse.Builder builder, UserProfile userProfile) {
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

  private void addInstitutionInfo(UpdateProfileResponse.Builder builder, UserProfile userProfile) {
    Optional.ofNullable(userProfile.getInstitutionId())
        .ifPresent(institutionId -> {
          try {
            String institutionName = getInstitutionName(institutionId);
            builder.setInstitution(UpdateProfileInstitution.newBuilder()
                .setId(institutionId.toString())
                .setName(institutionName));
          } catch (Exception e) {
            log.error("Error occurred while getting institution name: ", e);
            throw e;
          }
        });
  }

  private String getInstitutionName(UUID institutionId) {
    GetInstitutionNameResponse response = institutionServiceBlockingStub.getInstitutionName(
        GetInstitutionNameRequest.newBuilder()
            .setId(institutionId.toString())
            .build());
    return response.getName();
  }
}
