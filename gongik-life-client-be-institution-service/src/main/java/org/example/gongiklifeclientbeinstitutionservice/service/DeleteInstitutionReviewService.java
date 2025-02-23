package org.example.gongiklifeclientbeinstitutionservice.service;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewResponse;
import io.grpc.Status;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeinstitutionservice.entity.Institution;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReview;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteInstitutionReviewService {

  private final InstitutionReviewRepository institutionReviewRepository;
  private final InstitutionRepository institutionRepository;

  @Transactional
  public DeleteInstitutionReviewResponse deleteInstitutionReview(
      DeleteInstitutionReviewRequest request) {
    InstitutionReview institutionReview = findInstitutionReview(request.getInstitutionReviewId());
    validateReviewOwnership(institutionReview, request.getUserId());

    Institution institution = findInstitution(institutionReview.getInstitution().getId());
    decreaseReviewCount(institution);
    softDeleteReview(institutionReview);

    return createSuccessResponse();
  }

  private InstitutionReview findInstitutionReview(String reviewId) {
    return institutionReviewRepository.findById(UUID.fromString(reviewId))
        .orElseThrow(() -> Status.NOT_FOUND
            .withDescription("Institution review not found, wrong institution review id")
            .asRuntimeException());
  }

  private void validateReviewOwnership(InstitutionReview review, String userId) {
    if (!review.getUserId().equals(UUID.fromString(userId))) {
      throw Status.PERMISSION_DENIED
          .withDescription("You can delete only your institution review")
          .asRuntimeException();
    }
  }

  private Institution findInstitution(UUID institutionId) {
    return institutionRepository.findById(institutionId)
        .orElseThrow(() -> Status.NOT_FOUND
            .withDescription("Institution not found, wrong institution id")
            .asRuntimeException());
  }

  private void decreaseReviewCount(Institution institution) {
    institution.setReviewCount(institution.getReviewCount() - 1);
    institutionRepository.save(institution);
  }

  private void softDeleteReview(InstitutionReview review) {
    review.setDeletedAt(new Date());
    institutionReviewRepository.save(review);
  }

  private DeleteInstitutionReviewResponse createSuccessResponse() {
    return DeleteInstitutionReviewResponse.newBuilder()
        .setSuccess(true)
        .build();
  }
}

