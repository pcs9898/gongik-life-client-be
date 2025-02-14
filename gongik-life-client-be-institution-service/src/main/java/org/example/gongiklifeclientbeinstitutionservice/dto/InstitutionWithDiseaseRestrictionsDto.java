package org.example.gongiklifeclientbeinstitutionservice.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.gongiklifeclientbeinstitutionservice.entity.Institution;

@Data
@AllArgsConstructor
public class InstitutionWithDiseaseRestrictionsDto {

  private Institution institution;
  private List<String> diseaseRestrictions;
}