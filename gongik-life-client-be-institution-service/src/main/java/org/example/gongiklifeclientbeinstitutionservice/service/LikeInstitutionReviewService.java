package org.example.gongiklifeclientbeinstitutionservice.service;

import dto.institution.LikeInstitutionReviewRequestDto;
import io.grpc.Status;
import java.time.LocalDateTime;
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
public class LikeInstitutionReviewService {

  private final InstitutionReviewRepository institutionReviewRepository;
  private final InstitutionReviewLikeRepository institutionReviewLikeRepository;

  @Transactional
  public void likeInstitutionReview(LikeInstitutionReviewRequestDto requestDto) {
    UUID reviewId = parseUUID(requestDto.getInstitutionReviewId());
    UUID userId = parseUUID(requestDto.getUserId());

    InstitutionReview review = findReviewById(reviewId);
    validateNotAlreadyLiked(reviewId, userId);
    saveLikeAndUpdateCount(review, userId);
  }

  private UUID parseUUID(String id) {
    return UUID.fromString(id);
  }

  private InstitutionReview findReviewById(UUID reviewId) {
    return institutionReviewRepository.findById(reviewId)
        .orElseThrow(() -> createNotFoundException(
            "Institution review not found, wrong institution review id"));
  }

  private void validateNotAlreadyLiked(UUID reviewId, UUID userId) {
    if (institutionReviewLikeRepository.existsByIdInstitutionReviewIdAndIdUserId(reviewId,
        userId)) {
      throw createInvalidArgumentException("User already liked this review");
    }
  }

  private void saveLikeAndUpdateCount(InstitutionReview review, UUID userId) {
    InstitutionReviewLike newLike = createNewLike(review.getId(), userId);
    institutionReviewLikeRepository.save(newLike);
    review.setLikeCount(review.getLikeCount() + 1);
  }

  private InstitutionReviewLike createNewLike(UUID reviewId, UUID userId) {
    return new InstitutionReviewLike(
        new InstitutionReviewLikeId(reviewId, userId),
        LocalDateTime.now()
    );
  }

  private RuntimeException createNotFoundException(String message) {
    return Status.NOT_FOUND.withDescription(message).asRuntimeException();
  }

  private RuntimeException createInvalidArgumentException(String message) {
    return Status.INVALID_ARGUMENT.withDescription(message).asRuntimeException();
  }
}
