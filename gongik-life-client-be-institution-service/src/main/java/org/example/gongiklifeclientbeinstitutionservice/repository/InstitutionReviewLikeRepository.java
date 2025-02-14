package org.example.gongiklifeclientbeinstitutionservice.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.UUID;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReviewLike;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReviewLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionReviewLikeRepository extends
    JpaRepository<InstitutionReviewLike, InstitutionReviewLikeId> {

  boolean existsByIdInstitutionReviewIdAndIdUserId(UUID institutionReviewId, UUID userId);

  @Query("SELECT COUNT(l) > 0 FROM InstitutionReviewLike l WHERE l.id.institutionReviewId = :reviewId AND l.id.userId = :userId")
  boolean existsByReviewIdAndUserId(@Param("reviewId") UUID reviewId, @Param("userId") UUID userId);


}
