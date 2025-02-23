package org.example.gongiklifeclientbeuserservice.service;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileInstitution;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileResponse;
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
public class MyProfileService {

  private final UserRepository userRepository;
  private final UserProfileRepository userProfileRepository;

  @GrpcClient("gongik-life-client-be-institution-service")
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionServiceBlockingStub;

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
}
