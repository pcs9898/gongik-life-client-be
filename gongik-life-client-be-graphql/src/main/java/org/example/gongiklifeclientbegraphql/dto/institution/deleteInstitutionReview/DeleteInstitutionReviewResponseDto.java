package org.example.gongiklifeclientbegraphql.dto.institution.deleteInstitutionReview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteInstitutionReviewResponseDto {

  private String institutionReviewId;
  private Boolean success;

  public static DeleteInstitutionReviewResponseDto fromProto(String institutionReviewId) {
    return DeleteInstitutionReviewResponseDto.builder()
        .institutionReviewId(institutionReviewId)
        .success(true)
        .build();
  }
}
