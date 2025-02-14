package org.example.gongiklifeclientbeinstitutionservice.repository;

import java.util.UUID;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReviewLike;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReviewLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionReviewLikeRepository extends
    JpaRepository<InstitutionReviewLike, InstitutionReviewLikeId> {

  boolean existsById_InstitutionReviewIdAndId_UserId(UUID institutionReviewId, UUID userId);

  void deleteById_InstitutionReviewIdAndId_UserId(UUID institutionReviewId, UUID userId);
}
