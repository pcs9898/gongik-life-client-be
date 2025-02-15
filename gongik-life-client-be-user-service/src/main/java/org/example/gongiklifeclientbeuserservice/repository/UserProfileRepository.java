package org.example.gongiklifeclientbeuserservice.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.gongiklifeclientbeuserservice.entity.User;
import org.example.gongiklifeclientbeuserservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

  Optional<UserProfile> findByUser(User user);

  List<UserProfile> findByUserIdIn(Collection<UUID> userIds);

//  @Query("SELECT up.name FROM UserProfile up WHERE up.user.id = :userId")
//  Optional<String> findNameByUserId(@Param("userId") UUID userId);
}
