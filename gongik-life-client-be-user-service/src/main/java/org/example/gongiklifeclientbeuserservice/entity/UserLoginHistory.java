package org.example.gongiklifeclientbeuserservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "user_login_histories")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserLoginHistory extends Auditable {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(name = "user_id")
  private UUID userId;  // User 엔티티 대신 ID만 저장

  @Column(name = "last_login_at", nullable = false)
  private LocalDateTime lastLoginAt;

  @Column(name = "ip_address", length = 45)
  private String ipAddress;

}
