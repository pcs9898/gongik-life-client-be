package org.example.gongiklifeclientbeinstitutionservice.repository;

import java.util.Optional;
import org.example.gongiklifeclientbeinstitutionservice.entity.RegionalMilitaryOffice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionalMilitaryOfficeRepository extends
    JpaRepository<RegionalMilitaryOffice, Integer> {

  Optional<RegionalMilitaryOffice> findByOfficeName(String prefix);
}