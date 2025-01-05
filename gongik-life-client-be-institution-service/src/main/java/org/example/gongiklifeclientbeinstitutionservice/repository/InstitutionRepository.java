package org.example.gongiklifeclientbeinstitutionservice.repository;

import org.example.gongiklifeclientbeinstitutionservice.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {

  @Query("SELECT COUNT(i) > 0 FROM Institution i")
  boolean existsAny();
}
