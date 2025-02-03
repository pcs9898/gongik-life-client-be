package org.example.gongiklifeclientbeinstitutionservice.repository;

import java.util.List;
import java.util.UUID;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionDiseaseRestriction;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionDiseaseRestrictionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InstitutionDiseaseRestrictionRepository extends
    JpaRepository<InstitutionDiseaseRestriction, InstitutionDiseaseRestrictionId> {


  @Query("SELECT idr.diseaseRestriction.id FROM InstitutionDiseaseRestriction idr WHERE idr.institution.id = :institutionId")
  List<Integer> findDiseaseIdsByInstitutionId(@Param("institutionId") UUID institutionId);

  List<InstitutionDiseaseRestriction> findByInstitutionId(UUID institutionId);
}