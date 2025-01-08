package org.example.gongiklifeclientbeuserservice.repository;

import org.example.gongiklifeclientbeuserservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Boolean existsByEmail(String email);

}
