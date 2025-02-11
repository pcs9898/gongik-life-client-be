package org.example.gongiklifeclientbeinstitutionservice.entity;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitution;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "institutions")
@Slf4j
@ToString(exclude = "diseaseRestrictions")
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

  @Column(name = "review_count", nullable = false)
  private Integer reviewCount;

  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @OneToMany(mappedBy = "institution", fetch = FetchType.LAZY)
  private Set<InstitutionDiseaseRestriction> diseaseRestrictions = new HashSet<>();


  @OneToMany(mappedBy = "institution")
  private Set<InstitutionReview> reviews;

  public UUID getId() {
    return this.id;
  }

  // Institution 엔티티에 추가
  public void addDiseaseRestriction(InstitutionDiseaseRestriction restriction) {
    diseaseRestrictions.add(restriction);
    restriction.setInstitution(this);
  }

  public void removeDiseaseRestriction(InstitutionDiseaseRestriction restriction) {
    diseaseRestrictions.remove(restriction);
    restriction.setInstitution(null);
  }


  public SearchInstitution toProto() {
    return SearchInstitution.newBuilder()
        .setId(id.toString())
        .setName(name)
        .setAddress(address)
        .build();
  }

  @Transactional
  public InstitutionResponse.Builder toInstitutionResponseProto() {
    InstitutionResponse.Builder builder = InstitutionResponse.newBuilder()
        .setId(id.toString())
        .setName(name)
        .setInstitutionCategoryId(institutionCategory.getId().intValue())
        .setAddress(address)
        .setPhoneNumber(phoneNumber)
        .setRegionalMilitaryOfficeId(regionalMilitaryOffice.getId().intValue())
        .setRegion(region)
        .setSexualCriminalRecordRestriction(sexualCriminalRecordRestriction)
        .setReviewCount(reviewCount);

    if (tag != null) {
      builder.setTagId(tag.getId().intValue());
    }
    if (parentInstitution != null) {
      builder.setParentInstitution(parentInstitution);
    }
    if (averageWorkhours != null) {
      builder.setAverageWorkhours(averageWorkhours);
    }
    if (averageRating != null) {
      builder.setAverageRating(averageRating);
    }
// not work with jpa relation
//    // diseaseRestrictions 컬렉션을 복사하여 순회
//    Set<InstitutionDiseaseRestriction> diseaseRestrictionsCopy;
//    synchronized (diseaseRestrictions) {
//      diseaseRestrictionsCopy = new HashSet<>(diseaseRestrictions);
//    }
//
//    log.info("diseaseRestrictions@@@ : {}", diseaseRestrictionsCopy);
//    if (diseaseRestrictionsCopy != null && !diseaseRestrictionsCopy.isEmpty()) {
//      int[] diseaseRestrictionIds = diseaseRestrictionsCopy.stream()
//          .mapToInt(restriction -> restriction.getDiseaseRestriction().getId())
//          .toArray();
//      builder.addAllDiseaseRestrictions(
//          Arrays.stream(diseaseRestrictionIds).boxed().collect(Collectors.toList()));
//    }

    return builder;
  }
}