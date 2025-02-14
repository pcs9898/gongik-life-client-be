package org.example.gongiklifeclientbeinstitutionservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class InstitutionReviewLikeId implements Serializable {

  @Column(name = "institution_review_id")
  private UUID institutionReviewId;

  @Column(name = "user_id")
  private UUID userId;


}
