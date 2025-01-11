package org.example.gongiklifeclientbeuserservice.repository;

import org.example.gongiklifeclientbeuserservice.entity.AuthType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthTypeRepository extends JpaRepository<AuthType, Integer> {

}
