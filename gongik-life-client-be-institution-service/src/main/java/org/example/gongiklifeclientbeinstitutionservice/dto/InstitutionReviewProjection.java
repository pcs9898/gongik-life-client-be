package org.example.gongiklifeclientbeinstitutionservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public interface InstitutionReviewProjection {

  UUID getId();

  UUID getInstitutionId();

  UUID getUserId();

  //  UUID getUserId();
  double getRating();


  String getMainTasks();

  String getProsCons();

  Integer getAverageWorkhours();

  Integer getLikeCount();

  LocalDateTime getCreatedAt();

  Boolean getIsLiked();
}
