package org.example.gongiklifeclientbeuserservice.repository;


import java.util.UUID;
import org.example.gongiklifeclientbeuserservice.entity.UserLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserLoginHistoryRepository extends JpaRepository<UserLoginHistory, UUID> {

  @Transactional
  @Query("INSERT INTO UserLoginHistory (userId, ipAddress, lastLoginAt) VALUES (:userId, :ipAddress, CURRENT_TIMESTAMP)")
  @Modifying
  void saveLoginHistory(@Param("userId") UUID userId, @Param("ipAddress") String ipAddress);
}
