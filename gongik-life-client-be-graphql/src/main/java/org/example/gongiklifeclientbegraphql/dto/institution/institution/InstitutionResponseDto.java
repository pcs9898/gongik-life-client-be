package org.example.gongiklifeclientbegraphql.dto.institution.institution;


import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionResponseDto {

  private String id;
  private String name;
  private Integer institutionCategoryId;
  private String address;
  private String phoneNumber;
  private Integer tagId;
  private Integer regionalMilitaryOfficeId;
  private String region;
  private String parentInstitution;
  private Boolean sexualCriminalRecordRestriction;
  private Integer averageWorkhours;
  private Float averageRating;
  private Integer reviewCount;
  private List<Integer> diseaseRestrictions;

  public static InstitutionResponseDto fromInstitutionResponseProto(
      InstitutionResponse institution) {
    return InstitutionResponseDto.builder()
        .id(institution.getId())
        .name(institution.getName())
        .institutionCategoryId(institution.getInstitutionCategoryId())
        .address(institution.getAddress())
        .phoneNumber(institution.getPhoneNumber())
        .regionalMilitaryOfficeId(institution.getRegionalMilitaryOfficeId())
        .region(institution.getRegion())
        .sexualCriminalRecordRestriction(institution.getSexualCriminalRecordRestriction())
        .reviewCount(institution.getReviewCount())
        .tagId(institution.hasTagId() ? institution.getTagId() : null)
        .parentInstitution(
            institution.hasParentInstitution() ? institution.getParentInstitution() : null)
        .averageWorkhours(
            institution.hasAverageWorkhours() ? institution.getAverageWorkhours() : null)
        .averageRating(
            institution.hasAverageRating() ? (float) institution.getAverageRating() : null)
        .diseaseRestrictions(
            institution.getDiseaseRestrictionsList().isEmpty() ? null
                : institution.getDiseaseRestrictionsList())
        .build();
  }
}
