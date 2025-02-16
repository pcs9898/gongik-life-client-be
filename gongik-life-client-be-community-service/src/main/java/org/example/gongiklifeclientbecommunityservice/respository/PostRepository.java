package org.example.gongiklifeclientbecommunityservice.respository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.gongiklifeclientbecommunityservice.dto.PostProjection;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

//  // @Modifying: DB를 수정하는 쿼리임을 나타냄
//  // @Query: 직접 쿼리를 작성
//  // @Transactional: 트랜잭션 처리
//  @Modifying
//  @Query("UPDATE Post p SET p.deletedAt = CURRENT_TIMESTAMP WHERE p.id = :id AND p.userId = :userId")
//  @Transactional
//  int softDeleteByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

  Optional<Post> findByIdAndUserId(UUID id, UUID userId);

  @Query("SELECT p.commentCount FROM Post p WHERE p.id = :postId AND p.deletedAt IS NULL")
  Integer findCommentCountById(@Param("postId") UUID postId);

  @Query(value = """
      SELECT 
          p.id,
          p.user_id as userId,
          p.category_id as categoryId,
          p.title,
          p.content,
          p.like_count as likeCount,
          p.comment_count as commentCount,
          p.created_at as createdAt,
          CASE 
              WHEN :userId IS NULL THEN false
              ELSE EXISTS (
                  SELECT 1 
                  FROM post_likes pl 
                  WHERE pl.post_id = p.id 
                  AND pl.user_id = :userId
              )
          END as isLiked
      FROM posts p
      WHERE 
          p.deleted_at IS NULL
          AND (CAST(:categoryId AS integer) > 6 OR p.category_id = :categoryId)
          AND (
              CAST(:cursor AS uuid) IS NULL 
              OR 
              (p.created_at, p.id) < (
                  SELECT created_at, id 
                  FROM posts 
                  WHERE id = :cursor
              )
          )
      ORDER BY p.created_at DESC, p.id DESC
      LIMIT :limit
      """,
      nativeQuery = true)
  List<PostProjection> findPostsWithCursor(
      @Param("userId") UUID userId,
      @Param("categoryId") Integer categoryId,
      @Param("cursor") UUID cursor,
      @Param("limit") int limit
  );

  @Modifying
  @Query("UPDATE Post p SET p.commentCount = p.commentCount + 1 WHERE p.id = :postId")
  void plusCommentCountById(@Param("postId") UUID postId);

  @Modifying
  @Query("UPDATE Post p SET p.commentCount = p.commentCount - 1 WHERE p.id = :postId")
  void minusCommentCountById(@Param("postId") UUID postId);


  @Query(value = """
      SELECT 
          p.id,
          p.user_id as userId,
          p.category_id as categoryId,
          p.title,
          p.content,
          p.like_count as likeCount,
          p.comment_count as commentCount,
          p.created_at as createdAt,
          CASE 
              WHEN :userId IS NULL THEN false
              ELSE EXISTS (
                  SELECT 1 
                  FROM post_likes pl 
                  WHERE pl.post_id = p.id 
                  AND pl.user_id = :userId
              )
          END as isLiked
      FROM posts p
      WHERE 
          p.deleted_at IS NULL
          AND p.user_id = :userId
          AND (
              CAST(:cursor AS uuid) IS NULL 
              OR 
              (p.created_at, p.id) < (
                  SELECT created_at, id
                  FROM posts 
                  WHERE id = :cursor
              )
          )
      ORDER BY p.created_at DESC, p.id DESC
      LIMIT :limit
      """, nativeQuery = true)
  List<PostProjection> findMyPostsWithCursor(
      @Param("userId") UUID userId,
      @Param("cursor") UUID cursor,
      @Param("limit") int limit
  );

  @Query(value = """
      SELECT
      p.id,
      p.user_id AS userId,
      p.category_id AS categoryId,
      p.title,
      p.content,
      p.like_count AS likeCount,
      p.comment_count AS commentCount,
      p.created_at AS createdAt,
      CASE
      WHEN :myUserId IS NULL THEN false
      ELSE EXISTS (
      SELECT 1
      FROM post_likes pl
      WHERE pl.post_id = p.id
      AND pl.user_id = :myUserId
      )
      END AS isLiked
      FROM posts p
      WHERE
      p.deleted_at IS NULL
      AND p.user_id = :userId
      AND (
      CAST(:cursor AS uuid) IS NULL
      OR (p.created_at, p.id) < (
      SELECT created_at, id
      FROM posts
      WHERE id = :cursor
      )
      )
      ORDER BY p.created_at DESC, p.id DESC
      LIMIT :limit
      """, nativeQuery = true)
  List<PostProjection> findPostsByUserWithCursor(
      @Param("userId") UUID userId,
      @Param("myUserId") UUID myUserId,
      @Param("cursor") UUID cursor,
      @Param("limit") int limit
  );

  @Query(value = """
      SELECT
      p.id,
      p.user_id AS userId,
      p.category_id AS categoryId,
      p.title,
      p.content,
      p.like_count AS likeCount,
      p.comment_count AS commentCount,
      p.created_at AS createdAt,
      true AS isLiked
      FROM posts p
      WHERE
      p.deleted_at IS NULL
      AND EXISTS (
      SELECT 1
      FROM post_likes pl
      WHERE pl.post_id = p.id
      AND pl.user_id = :userId
      )
      AND (
      CAST(:cursor AS uuid) IS NULL
      OR (p.created_at, p.id) < (
      SELECT created_at, id
      FROM posts
      WHERE id = :cursor
      )
      )
      ORDER BY p.created_at DESC, p.id DESC
      LIMIT :limit
      """, nativeQuery = true)
  List<PostProjection> findMyLikedPostsWithCursor(
      @Param("userId") UUID userId,
      @Param("cursor") UUID cursor,
      @Param("limit") int limit
  );

  @Query(value = """
      SELECT 
          p.id,
          p.user_id as "userId",
          p.category_id as "categoryId",
          p.title,
          p.content,
          p.like_count as "likeCount",
          p.comment_count as "commentCount",
          p.created_at as "createdAt",
          CASE 
              WHEN :userId IS NULL THEN false
              ELSE EXISTS (
                  SELECT 1 
                  FROM post_likes pl 
                  WHERE pl.post_id = p.id 
                    AND pl.user_id = :userId
              )
          END as "isLiked"
      FROM posts p
      WHERE 
          p.deleted_at IS NULL
          -- 제목에 검색어가 포함되어 있는지 확인 (대소문자 구분 여부는 DB 설정에 따름)
          AND p.title LIKE CONCAT('%', :searchKeyword, '%')
          -- postCategoryId가 7이면 전체 검색, 아니면 특정 카테고리만 검색
          AND (:postCategoryId = 7 OR p.category_id = :postCategoryId)
          -- 커서 페이징: cursor 값이 존재하면 cursor를 기준으로 최근(created_at, id)의 복합 정렬 조건 적용
          AND (:cursor IS NULL OR (p.created_at, p.id) < (
              SELECT p2.created_at, p2.id
              FROM posts p2
              WHERE p2.id = :cursor
          ))
      ORDER BY p.created_at DESC, p.id DESC
      LIMIT :pageSize
      """, nativeQuery = true)
  List<PostProjection> searchPosts(
      @Param("searchKeyword") String searchKeyword,
      @Param("postCategoryId") int postCategoryId,
      @Param("cursor") UUID cursor,
      @Param("pageSize") int pageSize,
      @Param("userId") UUID userId
  );

}
