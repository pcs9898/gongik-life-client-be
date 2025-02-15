package org.example.gongiklifeclientbecommunityservice.respository;

import java.util.Optional;
import java.util.UUID;
import org.example.gongiklifeclientbecommunityservice.entity.PostLike;
import org.example.gongiklifeclientbecommunityservice.entity.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {

  // userId와 postId로 존재 여부 확인
  boolean existsByIdPostIdAndIdUserId(UUID postId, UUID userId);


  // 또는 Optional로 반환받기
  Optional<PostLike> findById_UserIdAndId_PostId(UUID userId, UUID postId);
}
