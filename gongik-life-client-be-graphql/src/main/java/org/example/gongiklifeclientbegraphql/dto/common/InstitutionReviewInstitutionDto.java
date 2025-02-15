package org.example.gongiklifeclientbegraphql.dto.common;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewInstitution;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class InstitutionReviewInstitutionDto {

  private String institutionId;
  private String institutionName;
  private Integer institutionCategoryId;

  public static InstitutionReviewInstitutionDto fromProto(
      InstitutionReviewInstitution institution) {

    return InstitutionReviewInstitutionDto.builder()
        .institutionId(institution.getInstitutionId())
        .institutionName(institution.getInstitutionName())
        .institutionCategoryId(institution.getInstitutionCategoryId())
        .build();
  }
}
