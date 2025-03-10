package org.example.gongiklifeclientbenotificationservice.repository;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.gongiklifeclientbenotificationservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("UPDATE Notification n " +
      "SET n.readAt = CURRENT_TIMESTAMP " +
      "WHERE n.userId = :userId " +
      "  AND n.deletedAt IS NULL " +
      "  AND n.readAt IS NULL")
  int markAllNotificationsAsRead(@Param("userId") UUID userId);


  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("UPDATE Notification n " +
      "SET n.deletedAt = CURRENT_TIMESTAMP " +
      "WHERE n.userId = :userId " +
      "  AND n.deletedAt IS NULL")
  int deleteAllNotifications(@Param("userId") UUID userId);
}
