package org.example.gongiklifeclientbeinstitutionservice.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.UUID;
import org.example.gongiklifeclientbeinstitutionservice.entity.Institution;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, UUID> {

  @Query("SELECT COUNT(i) > 0 FROM Institution i")
  boolean existsAny();

  List<Institution> findByNameContainingAndIdGreaterThanOrderByIdAsc(String name, UUID id,
      Pageable pageable);

  List<Institution> findByNameContainingOrderByIdAsc(String name, Pageable pageable);

  @Query("SELECT i.reviewCount FROM Institution i WHERE i.id = :institutionId AND i.deletedAt IS NULL")
  int getReviewCount(@Param("institutionId") UUID institutionId);

}
