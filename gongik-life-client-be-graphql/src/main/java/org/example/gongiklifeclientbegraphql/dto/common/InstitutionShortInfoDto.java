package org.example.gongiklifeclientbegraphql.dto.common;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionShortInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionShortInfoDto {

  private String institutionId;
  private String institutionName;

  public static InstitutionShortInfoDto fromProto(InstitutionShortInfo institution) {
    return InstitutionShortInfoDto.builder()
        .institutionId(institution.getInstitutionId())
        .institutionName(institution.getInstitutionName())
        .build();
  }
}
