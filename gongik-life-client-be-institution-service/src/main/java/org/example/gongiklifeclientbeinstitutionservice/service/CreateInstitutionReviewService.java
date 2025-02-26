package org.example.gongiklifeclientbeinstitutionservice.service;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.CreateInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewResponse;
import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.CheckUserInstitutionRequest;
import io.grpc.Status;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbeinstitutionservice.entity.Institution;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReview;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateInstitutionReviewService {

  private final InstitutionRepository institutionRepository;
  private final InstitutionReviewRepository institutionReviewRepository;

  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

  @Transactional
  public InstitutionReviewResponse createInstitutionReview(CreateInstitutionReviewRequest request) {
    Institution institution = findInstitutionById(request.getInstitutionId());
    validateUserInstitution(request);
    validateNoDuplicateReview(request);

    Double averageRating = calculateAverageRating(request);
    updateInstitutionReviewCount(institution);

    return saveInstitutionReview(request, institution, averageRating);
  }

  private Institution findInstitutionById(String institutionId) {
    return institutionRepository.findById(UUID.fromString(institutionId))
        .orElseThrow(() -> Status.INVALID_ARGUMENT
            .withDescription("Institution not found, wrong institution id")
            .asRuntimeException());
  }

  private void validateUserInstitution(CreateInstitutionReviewRequest request) {
    String userName = getUserNameFromUserService(request);
    if (userName.isEmpty()) {
      throw Status.INVALID_ARGUMENT
          .withDescription("User and institution does not match")
          .asRuntimeException();
    }
  }

  private String getUserNameFromUserService(CreateInstitutionReviewRequest request) {
    return userServiceBlockingStub.checkUserInstitution(
        CheckUserInstitutionRequest.newBuilder()
            .setUserId(request.getUserId())
            .setInstitutionId(request.getInstitutionId())
            .build()
    ).getUserName();
  }

  private void validateNoDuplicateReview(CreateInstitutionReviewRequest request) {
    boolean hasExistingReview = institutionReviewRepository.existsByUserIdAndInstitutionId(
        UUID.fromString(request.getUserId()),
        UUID.fromString(request.getInstitutionId())
    );

    if (hasExistingReview) {
      throw Status.INVALID_ARGUMENT
          .withDescription("User already reviewed this institution")
          .asRuntimeException();
    }
  }

  private Double calculateAverageRating(CreateInstitutionReviewRequest request) {
    return (double) (
        request.getFacilityRating() +
            request.getLocationRating() +
            request.getStaffRating() +
            request.getVisitorRating() +
            request.getVacationFreedomRating()
    ) / 5;
  }

  private void updateInstitutionReviewCount(Institution institution) {
    institution.setReviewCount(institution.getReviewCount() + 1);
    institutionRepository.save(institution);
  }

  private InstitutionReviewResponse saveInstitutionReview(
      CreateInstitutionReviewRequest request,
      Institution institution,
      Double rating
  ) {
    String userName = getUserNameFromUserService(request);
    InstitutionReview review = InstitutionReview.fromProto(request, institution, rating);
    return institutionReviewRepository.save(review).toProto(userName);
  }
}
