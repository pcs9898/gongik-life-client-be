package org.example.gongiklifeclientbereportservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reports")
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at is null")
public class Report extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(columnDefinition = "UUID")
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "type_id", nullable = false)
  private Integer typeId;

  @Column(name = "system_category_id")
  private Integer systemCategoryId;

  @Column(name = "title", length = 100, nullable = false)
  private String title;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "status_id", nullable = false)
  private Integer statusId;

  @Column(name = "target_id")
  private UUID targetId;

  @Column(name = "deleted_at")
  private Date deletedAt;


}
