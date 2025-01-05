package org.example.gongiklifeclientbeinstitutionservice.repository;

import java.util.Optional;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionTagRepository extends JpaRepository<InstitutionTag, Long> {

  Optional<InstitutionTag> findByTagName(String tagName);
}