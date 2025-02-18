package org.example.gongiklifeclientbenotificationservice.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.gongiklifeclientbenotificationservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  @Query(value = "SELECT * FROM notifications " +
      "WHERE user_id = CAST(:userId AS uuid) " +
      " AND deleted_at IS NULL " +
      " AND ( :cursor IS NULL OR " +
      " (created_at < (SELECT created_at FROM notifications WHERE id = CAST(:cursor AS uuid))) OR "
      +
      " (created_at = (SELECT created_at FROM notifications WHERE id = CAST(:cursor AS uuid)) " +
      " AND id < CAST(:cursor AS uuid)) " +
      " ) " +
      "ORDER BY created_at DESC, id DESC " +
      "LIMIT :pageSize", nativeQuery = true)
  List<Notification> findMyNotificationsWithCursor(@Param("userId") String userId,
      @Param("cursor") String cursor,
      @Param("pageSize") int pageSize);

  Optional<Notification> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);

}
