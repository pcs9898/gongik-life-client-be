package org.example.gongiklifeclientbeinstitutionservice.batch.writer;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.gongiklifeclientbeinstitutionservice.dto.InstitutionWithDiseaseRestrictionsDto;
import org.example.gongiklifeclientbeinstitutionservice.entity.Institution;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionDiseaseRestriction;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionDiseaseRestrictionId;
import org.example.gongiklifeclientbeinstitutionservice.repository.DiseaseRestrictionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionDiseaseRestrictionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstitutionItemWriter implements ItemWriter<InstitutionWithDiseaseRestrictionsDto> {

  private final InstitutionRepository institutionRepository;
  private final DiseaseRestrictionRepository diseaseRestrictionRepository;
  private final InstitutionDiseaseRestrictionRepository institutionDiseaseRestrictionRepository;

  @Override
  public void write(Chunk<? extends InstitutionWithDiseaseRestrictionsDto> items) {
    for (InstitutionWithDiseaseRestrictionsDto item : items) {
      Institution savedInstitution = institutionRepository.save(item.getInstitution());

      List<String> diseaseRestrictions = item.getDiseaseRestrictions();
      if (diseaseRestrictions != null && !diseaseRestrictions.isEmpty()) {
        for (String diseaseName : diseaseRestrictions) {
          diseaseRestrictionRepository.findByDiseaseName(diseaseName).ifPresent(disease -> {
            InstitutionDiseaseRestrictionId id = new InstitutionDiseaseRestrictionId(
                savedInstitution.getId(), disease.getId());
            InstitutionDiseaseRestriction institutionDiseaseRestriction = new InstitutionDiseaseRestriction(
                id, savedInstitution, disease);
            institutionDiseaseRestrictionRepository.save(institutionDiseaseRestriction);
          });
        }
      }
    }
  }
}