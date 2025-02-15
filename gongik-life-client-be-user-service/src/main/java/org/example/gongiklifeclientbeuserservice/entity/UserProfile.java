package org.example.gongiklifeclientbeuserservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "user_profiles", uniqueConstraints = {
    @UniqueConstraint(name = "fk_user_profile", columnNames = "user_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted_at is null")
public class UserProfile extends Auditable {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "institution_id")
  private UUID institutionId;

  @Column(name = "name", nullable = false, length = 30)
  private String name;

  @Column(name = "bio", length = 30)
  private String bio;

  @Column(name = "enlistment_date")
  private Date enlistmentDate;

  @Column(name = "discharge_date")
  private Date dischargeDate;

  @Column(name = "deleted_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Date deletedAt;


  public String getBio() {
    return bio != null ? bio : "";
  }
}