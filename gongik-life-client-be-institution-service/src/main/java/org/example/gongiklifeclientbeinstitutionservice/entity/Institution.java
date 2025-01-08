package org.example.gongiklifeclientbeinstitutionservice.entity;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitution;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "institutions")
public class Institution {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(nullable = false, length = 100)
  private String name;

  @ManyToOne
  @JoinColumn(name = "institution_category_id", nullable = false)
  private InstitutionCategory institutionCategory;

  @Column(nullable = false, length = 255)
  private String address;

  @Column(nullable = false, length = 20)
  private String phoneNumber;

  @ManyToOne
  @JoinColumn(name = "tag_id")
  private InstitutionTag tag;

  @ManyToOne
  @JoinColumn(name = "regional_military_office_id", nullable = false)
  private RegionalMilitaryOffice regionalMilitaryOffice;

  @Column(nullable = false, length = 100)
  private String region;

  @Column(name = "parent_institution", length = 100)
  private String parentInstitution;

  @Column(name = "sexual_criminal_record_restriction", nullable = false)
  private boolean sexualCriminalRecordRestriction;

  @Column(name = "average_workhours")
  private Integer averageWorkhours;

  @Column(name = "average_rating")
  private Double averageRating;

  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @OneToMany(mappedBy = "institution")
  private Set<InstitutionDiseaseRestriction> diseaseRestrictions;


  @OneToMany(mappedBy = "institution")
  private Set<InstitutionReview> reviews;

  public UUID getId() {
    return this.id;
  }

  public SearchInstitution toProto() {
    return SearchInstitution.newBuilder()
        .setId(id.toString())
        .setName(name)
        .setAddress(address)
        .build();
  }
}