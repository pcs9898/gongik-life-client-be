package org.example.gongiklifeclientbeinstitutionservice.repository;


import java.util.List;
import java.util.UUID;
import org.example.gongiklifeclientbeinstitutionservice.dto.InstitutionReviewProjection;
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

  @Query(value = """
        SELECT 
            ir.id,
            ir.institution_id as institutionId,
            i.name as institutionName,
          i.institution_category_id as institutionCategoryId,
            ir.user_id as userId,
            ir.rating,
            ir.main_tasks as mainTasks,
            ir.pros_cons as prosCons,
            ir.average_workhours as averageWorkhours,
            ir.like_count as likeCount,
            ir.created_at as createdAt,
            CASE 
                WHEN :userId IS NULL THEN false
                ELSE EXISTS (
                    SELECT 1 
                    FROM institution_review_likes irl 
                    WHERE irl.institution_review_id = ir.id 
                    AND irl.user_id = :userId
                )
            END as isLiked
        FROM institution_reviews ir
        INNER JOIN institutions i ON ir.institution_id = i.id
        WHERE\s
        ir.deleted_at IS NULL
        AND (CAST(:categoryId AS integer) = 7 OR i.institution_category_id = :categoryId)
        AND (
            CAST(:cursor AS uuid) IS NULL\s
            OR\s
            (ir.created_at, ir.id) < (
                SELECT created_at, id\s
                FROM institution_reviews\s
                WHERE id = :cursor
            )
      )
        ORDER BY ir.created_at DESC, ir.id DESC
        LIMIT :limit
      """,
      nativeQuery = true)
  List<InstitutionReviewProjection> findReviewsWithCursor(
      @Param("userId") UUID userId,
      @Param("categoryId") Integer categoryId,
      @Param("cursor") UUID cursor,
      @Param("limit") int limit
  );

  @Query(value = """
      SELECT 
          ir.id,
          ir.institution_id as institutionId,
          i.name as institutionName,
          i.institution_category_id as institutionCategoryId,
          ir.user_id as userId,
          ir.rating,
          ir.main_tasks as mainTasks,
          ir.pros_cons as prosCons,
          ir.average_workhours as averageWorkhours,
          ir.like_count as likeCount,
          ir.created_at as createdAt,
          CASE 
              WHEN :userId IS NULL THEN false
              ELSE EXISTS (
                  SELECT 1 
                  FROM institution_review_likes irl 
                  WHERE irl.institution_review_id = ir.id 
                  AND irl.user_id = :userId
              )
          END as isLiked
      FROM institution_reviews ir
      INNER JOIN institutions i ON ir.institution_id = i.id
      WHERE 
          ir.deleted_at IS NULL
          AND ir.user_id = :userId
      ORDER BY ir.created_at DESC, ir.id DESC
      LIMIT 10
      """,
      nativeQuery = true)
  List<InstitutionReviewProjection> findMyInstitutionReviews(@Param("userId") UUID userId);

  @Query(value = """
      SELECT 
          ir.id,
          ir.institution_id as institutionId,
          i.name as institutionName,
          i.institution_category_id as institutionCategoryId,
          ir.user_id as userId,
          ir.rating,
          ir.main_tasks as mainTasks,
          ir.pros_cons as prosCons,
          ir.average_workhours as averageWorkhours,
          ir.like_count as likeCount,
          ir.created_at as createdAt,
          false as isLiked
      FROM institution_reviews ir
      INNER JOIN institutions i ON ir.institution_id = i.id
      WHERE 
          ir.deleted_at IS NULL
          AND ir.institution_id = :institutionId
          AND (
              CAST(:cursor AS uuid) IS NULL 
              OR 
              (ir.created_at, ir.id) < (
                  SELECT created_at, id 
                  FROM institution_reviews 
                  WHERE id = :cursor
              )
          )
      ORDER BY ir.created_at DESC, ir.id DESC
      LIMIT :limit
      """,
      nativeQuery = true)
  List<InstitutionReviewProjection> findInstitutionReviewsByInstitutionIdWithCursor(
      @Param("institutionId") UUID institutionId,
      @Param("cursor") UUID cursor,
      @Param("limit") int limit
  );


}
