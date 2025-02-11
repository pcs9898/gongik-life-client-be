package org.example.gongiklifeclientbegraphql.dto.deleteInstitutionReview;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteInstitutionReviewRequestDto {

  private String institutionReviewId;
  private String userId;

  public DeleteInstitutionReviewRequest toProto() {
    return DeleteInstitutionReviewRequest.newBuilder()
        .setInstitutionReviewId(institutionReviewId)
        .setUserId(userId)
        .build();
  }
}
