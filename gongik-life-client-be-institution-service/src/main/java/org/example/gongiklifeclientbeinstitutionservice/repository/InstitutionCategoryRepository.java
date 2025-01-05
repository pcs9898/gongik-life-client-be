package org.example.gongiklifeclientbeinstitutionservice.repository;

import java.util.Optional;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionCategoryRepository extends JpaRepository<InstitutionCategory, Long> {

  Optional<InstitutionCategory> findByCategoryName(String categoryName);
}