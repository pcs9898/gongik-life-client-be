package org.example.gongiklifeclientbeinstitutionservice.repository;

import java.util.UUID;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionReviewRepository extends JpaRepository<InstitutionReview, UUID> {

  @Query("SELECT COUNT(ir) > 0 FROM InstitutionReview ir WHERE ir.userId = :userId AND ir.institution.id = :institutionId")
  boolean existsByUserIdAndInstitutionId(@Param("userId") UUID userId,
      @Param("institutionId") UUID institutionId);

}
