package org.example.gongiklifeclientbegraphql.dto.institutionReviewsByInstitution;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsByInstitutionRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionReviewsByInstitutionRequestDto {

  private String userId;
  private String institutionId;
  private String cursor;
  private Integer pageSize;

  public InstitutionReviewsByInstitutionRequest toProto() {
    InstitutionReviewsByInstitutionRequest.Builder builder = InstitutionReviewsByInstitutionRequest.newBuilder()
        .setInstitutionId(institutionId)
        .setPageSize(pageSize);

    if (userId != null) {
      builder.setUserId(userId);
    }

    if (cursor != null) {
      builder.setCursor(cursor);
    }

    return builder.build();
  }
}
