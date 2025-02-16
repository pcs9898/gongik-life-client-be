package org.example.gongiklifeclientbecommunityservice.respository;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import java.util.List;
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

  // 재귀 쿼리를 사용해 해당 게시글의 루트 댓글과 대댓글을 최신순으로 가져옵니다.
  @Query(
      value = "WITH RECURSIVE comment_tree AS ( " +
          "  SELECT id, post_id, parent_comment_id, user_id, content, created_at, updated_at, deleted_at, "
          +
          "         TO_CHAR(created_at, 'YYYYMMDDHH24MISS') AS sort_key, " +
          "         1 AS level " +
          "  FROM comments " +
          "  WHERE parent_comment_id IS NULL AND post_id = :postId " +
          "  UNION ALL " +
          "  SELECT c.id, c.post_id, c.parent_comment_id, c.user_id, c.content, c.created_at, c.updated_at, c.deleted_at, "
          +
          "         ct.sort_key || '-' || TO_CHAR(c.created_at, 'YYYYMMDDHH24MISS') AS sort_key, " +
          "         ct.level + 1 AS level " +
          "  FROM comments c " +
          "  JOIN comment_tree ct ON c.parent_comment_id = ct.id " +
          ") " +
          "SELECT * FROM comment_tree ORDER BY sort_key ASC",
      nativeQuery = true
  )
  List<Comment> findCommentTreeByPostId(@Param("postId") UUID postId);
}



