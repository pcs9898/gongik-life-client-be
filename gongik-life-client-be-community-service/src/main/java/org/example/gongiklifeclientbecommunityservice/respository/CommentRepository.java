package org.example.gongiklifeclientbecommunityservice.respository;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import org.example.gongiklifeclientbecommunityservice.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

  // 방법 1: JPQL을 사용한 단일 쿼리 업데이트
  @Modifying
  @Query("UPDATE Comment c SET c.deletedAt = CURRENT_TIMESTAMP WHERE c.post.id = :postId AND c.deletedAt IS NULL")
  @Transactional
  int softDeleteAllByPostId(@Param("postId") UUID postId);

  @Query("SELECT c FROM Comment c WHERE c.id = :id AND c.deletedAt IS NULL")
  Optional<Comment> findByIdAndDeletedAtIsNull(@Param("id") UUID id);


}
