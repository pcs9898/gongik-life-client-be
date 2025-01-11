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

@Entity
@Table(name = "user_auths", uniqueConstraints = {
    @UniqueConstraint(name = "fk_user_auth", columnNames = {"user_id", "auth_type_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuth extends Auditable {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "auth_type_id", nullable = false)
  private int authTypeId;

  @Column(name = "auth_id", length = 255)
  private String authId;

  @Column(name = "password_hash", length = 255)
  private String passwordHash;

  @Column(name = "deleted_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Date deletedAt;
}