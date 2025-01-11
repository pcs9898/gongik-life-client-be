package org.example.gongiklifeclientbeuserservice.repository;

import java.util.UUID;
import org.example.gongiklifeclientbeuserservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

}
