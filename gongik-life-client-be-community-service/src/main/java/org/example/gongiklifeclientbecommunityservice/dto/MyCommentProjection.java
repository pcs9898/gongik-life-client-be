package org.example.gongiklifeclientbecommunityservice.dto;

import java.util.Date;
import java.util.UUID;

public interface MyCommentProjection {

  UUID getId();

  String getContent();

  Date getCreatedAt();

  UUID getPostId();

  String getPostTitle();
}
