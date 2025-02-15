package org.example.gongiklifeclientbecommunityservice.entity;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreatePostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreatePostResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.gongiklifeclientbecommunityservice.respository.Auditable;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "posts")
public class Post extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "category_id", nullable = false)
  private Integer categoryId;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "like_count", columnDefinition = "INT DEFAULT 0")
  private Integer likeCount = 0;

  @Column(name = "comment_count", columnDefinition = "INT DEFAULT 0")
  private Integer commentCount = 0;

  @Column(name = "deleted_at")
  private Date deletedAt;

  @OneToMany(mappedBy = "post")
  private List<Comment> comments = new ArrayList<>();


  public static Post fromProto(CreatePostRequest request) {
    return Post.builder()
        .userId(UUID.fromString(request.getUserId()))
        .categoryId(request.getCategoryId())
        .title(request.getTitle())
        .content(request.getContent())
        .likeCount(0)
        .commentCount(0)
        .build();
  }

  public CreatePostResponse toProto(String userName) {
    return CreatePostResponse.newBuilder()
        .setId(id.toString())
        .setUser(PostUser.newBuilder()
            .setUserId(userId.toString())
            .setUserName(userName)
            .build())
        .setCategoryId(categoryId)
        .setTitle(title)
        .setContent(content)
        .setLikeCount(likeCount)
        .setCommentCount(commentCount)
        .setCreatedAt(getCreatedAt().toString())
        .build();

  }
}