package org.example.gongiklifeclientbeinstitutionservice.service;

import dto.institution.UnlikeInstitutionReviewRequestDto;
import io.grpc.Status;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReview;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReviewLike;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReviewLikeId;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewLikeRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnlikeInstitutionReviewService {

  private final InstitutionReviewRepository institutionReviewRepository;
  private final InstitutionReviewLikeRepository institutionReviewLikeRepository;

  @Transactional
  public void unlikeInstitutionReview(UnlikeInstitutionReviewRequestDto requestDto) {
    UUID reviewId = parseUUID(requestDto.getInstitutionReviewId());
    UUID userId = parseUUID(requestDto.getUserId());

    InstitutionReview review = findReviewById(reviewId);
    InstitutionReviewLike like = findLikeByReviewAndUserId(review.getId(), userId);

    deleteLikeAndUpdateCount(like, review);
  }

  private UUID parseUUID(String id) {
    return UUID.fromString(id);
  }

  private InstitutionReview findReviewById(UUID reviewId) {
    return institutionReviewRepository.findById(reviewId)
        .orElseThrow(() -> createNotFoundException(
            "Institution review not found, wrong institution review id"));
  }

  private InstitutionReviewLike findLikeByReviewAndUserId(UUID reviewId, UUID userId) {
    return institutionReviewLikeRepository.findById(new InstitutionReviewLikeId(reviewId, userId))
        .orElseThrow(() -> createNotFoundException(
            "Institution review like not found, wrong institution review id or you didn't like this review"));
  }

  private void deleteLikeAndUpdateCount(InstitutionReviewLike like, InstitutionReview review) {
    institutionReviewLikeRepository.delete(like);
    review.setLikeCount(review.getLikeCount() - 1);
  }

  private RuntimeException createNotFoundException(String message) {
    return Status.NOT_FOUND.withDescription(message).asRuntimeException();
  }
}
