package org.example.gongiklifeclientbeinstitutionservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "institution_review_likes")
public class InstitutionReviewLike {

  @EmbeddedId
  private InstitutionReviewLikeId id;

  @Column(name = "created_at")
  private LocalDateTime createdAt;


}
