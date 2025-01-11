package org.example.gongiklifeclientbeuserservice.repository;

import java.util.UUID;
import org.example.gongiklifeclientbeuserservice.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth, UUID> {


}
