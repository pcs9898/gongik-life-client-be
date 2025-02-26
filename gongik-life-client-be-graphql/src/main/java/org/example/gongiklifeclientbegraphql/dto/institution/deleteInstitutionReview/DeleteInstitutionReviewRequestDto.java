package org.example.gongiklifeclientbegraphql.dto.institution.deleteInstitutionReview;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteInstitutionReviewRequestDto {

  @NotBlank
  private String institutionReviewId;

  private String userId;

  public DeleteInstitutionReviewRequest toProto() {
    return DeleteInstitutionReviewRequest.newBuilder()
        .setInstitutionReviewId(institutionReviewId)
        .setUserId(userId)
        .build();
  }
}
