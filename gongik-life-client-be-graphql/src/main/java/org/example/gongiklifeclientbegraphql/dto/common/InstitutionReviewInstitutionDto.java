package org.example.gongiklifeclientbegraphql.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionReviewInstitutionDto {

  private String institutionId;
  private String institutionName;
}
