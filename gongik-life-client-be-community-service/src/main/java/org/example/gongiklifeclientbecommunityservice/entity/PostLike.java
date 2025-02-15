package org.example.gongiklifeclientbecommunityservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "post_likes")
public class PostLike {

  @EmbeddedId
  private PostLikeId id;

//  @ManyToOne(fetch = FetchType.LAZY)
//  @MapsId("postId")
//  @JoinColumn(name = "post_id")
//  private Post post;

  @Column(name = "created_at")
  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;
}