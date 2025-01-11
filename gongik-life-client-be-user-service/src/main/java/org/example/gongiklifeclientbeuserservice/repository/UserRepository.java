package org.example.gongiklifeclientbeuserservice.repository;

import java.util.UUID;
import org.example.gongiklifeclientbeuserservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  Boolean existsByEmail(String email);

  @Modifying()
  @Query("UPDATE User u SET u.lastLoginAt = CURRENT_TIMESTAMP WHERE u.id = :id")
  @Transactional
  void updateLastLoginAt(@Param("id") UUID id);


}
