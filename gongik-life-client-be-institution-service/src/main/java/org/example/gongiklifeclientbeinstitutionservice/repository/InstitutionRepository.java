package org.example.gongiklifeclientbeinstitutionservice.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.UUID;
import org.example.gongiklifeclientbeinstitutionservice.dto.InstitutionForWorkHoursStatisticsProjection;
import org.example.gongiklifeclientbeinstitutionservice.dto.InstitutionSimpleProjection;
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

  @Query(value =
      "SELECT i.id as id, i.name as name, i.address as address, i.average_rating as averageRating "
          +
          "FROM institutions i " +
          "WHERE i.name LIKE CONCAT('%', :searchKeyword, '%') " +
          "  AND (:cursor IS NULL OR i.id > :cursor) " +
          "ORDER BY i.average_rating DESC " +
          "LIMIT :limit", nativeQuery = true)
  List<InstitutionSimpleProjection> searchInstitutions(
      @Param("searchKeyword") String searchKeyword,
      @Param("cursor") UUID cursor,
      @Param("limit") int limit);

  @Query("select i.id as id, i.institutionCategory.id as institutionCategoryId, " +
      "i.averageWorkhours as averageWorkhours, i.reviewCount as reviewCount " +
      "from Institution i " +
      "where i.averageWorkhours is not null")
  List<InstitutionForWorkHoursStatisticsProjection> findAllInstitutionsForWorkHoursStatistics();


}
