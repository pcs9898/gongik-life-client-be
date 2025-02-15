package org.example.gongiklifeclientbecommunityservice.dto;

import java.util.Date;
import java.util.UUID;

public interface PostProjection {

  UUID getId();

  UUID getUserId();

  Integer getCategoryId();

  String getTitle();

  String getContent();

  Integer getLikeCount();

  Integer getCommentCount();

  Date getCreatedAt();

  Boolean getIsLiked();
}
