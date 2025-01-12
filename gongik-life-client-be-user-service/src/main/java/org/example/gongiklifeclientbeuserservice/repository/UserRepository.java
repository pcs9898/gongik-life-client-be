package org.example.gongiklifeclientbeuserservice.repository;

import java.util.Optional;
import java.util.UUID;
import org.example.gongiklifeclientbeuserservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  Boolean existsByEmail(String email);


  Optional<User> findByEmail(String email);
}
