package org.example.gongiklifeclientbegraphql.dto.institution.institutionReview;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionReviewRequestDto {

  private String institutionReviewId;
  private String userId;

  public InstitutionReviewRequest toProto() {
    return InstitutionReviewRequest.newBuilder()
        .setInstitutionReviewId(institutionReviewId)
        .build();
  }
}
