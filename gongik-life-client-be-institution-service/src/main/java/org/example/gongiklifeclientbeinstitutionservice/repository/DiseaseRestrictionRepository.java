package org.example.gongiklifeclientbeinstitutionservice.repository;

import java.util.Optional;
import org.example.gongiklifeclientbeinstitutionservice.entity.DiseaseRestriction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiseaseRestrictionRepository extends JpaRepository<DiseaseRestriction, Integer> {

  Optional<DiseaseRestriction> findByDiseaseName(String diseaseName);
}