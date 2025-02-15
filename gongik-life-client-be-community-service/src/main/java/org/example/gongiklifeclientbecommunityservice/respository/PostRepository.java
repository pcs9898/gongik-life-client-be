package org.example.gongiklifeclientbecommunityservice.respository;

import java.util.Optional;
import java.util.UUID;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
