package org.example.gongiklifeclientbeinstitutionservice.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class InstitutionDiseaseRestrictionId implements Serializable {

  private UUID institutionId;
  private int diseaseId;
}