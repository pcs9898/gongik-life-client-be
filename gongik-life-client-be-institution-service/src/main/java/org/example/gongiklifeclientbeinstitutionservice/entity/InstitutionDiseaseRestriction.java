package org.example.gongiklifeclientbeinstitutionservice.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "institution_disease_restrictions")
public class InstitutionDiseaseRestriction {

  @EmbeddedId
  private InstitutionDiseaseRestrictionId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("institutionId")
  @JoinColumn(name = "institution_id", nullable = false)
  private Institution institution;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("diseaseId")
  @JoinColumn(name = "disease_id", nullable = false)
  private DiseaseRestriction diseaseRestriction;
}