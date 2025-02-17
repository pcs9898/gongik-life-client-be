package org.example.gongiklifeclientbereportservice.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.UUID;
import org.example.gongiklifeclientbereportservice.dto.ReportProjection;
import org.example.gongiklifeclientbereportservice.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

  boolean existsByUserIdAndTypeIdAndSystemCategoryId(UUID userId, int typeId, int systemCategoryId);

  boolean existsByUserIdAndTargetId(UUID userId, UUID targetId);

  @Query(value = "select cast(r.id as text) as id, " +
      "r.type_id as typeId, " +
      "r.system_category_id as systemCategoryId, " +
      "cast(r.target_id as text) as targetId, " +
      "r.status_id as statusId, " +
      "r.title as title, " +
      "to_char(r.created_at, 'YYYY-MM-DD HH24:MI:SS') as createdAt " +
      "from reports r " +
      "where r.user_id = cast(:userId as uuid) " +
      "  and r.deleted_at is null " +
      "  and (:cursor is null or " +
      "       r.created_at < (select r2.created_at from reports r2 where r2.id = cast(:cursor as uuid))"
      +
      "  ) " +
      "order by r.created_at desc " +
      "limit :pageSize", nativeQuery = true)
  List<ReportProjection> myReportsWithCursor(@Param("userId") String userId,
      @Param("cursor") String cursor,
      @Param("pageSize") int pageSize);

}
