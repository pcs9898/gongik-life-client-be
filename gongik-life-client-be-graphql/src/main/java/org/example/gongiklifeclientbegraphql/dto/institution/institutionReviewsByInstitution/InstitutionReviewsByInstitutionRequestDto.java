package org.example.gongiklifeclientbegraphql.dto.institution.institutionReviewsByInstitution;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsByInstitutionRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionReviewsByInstitutionRequestDto {

  private String userId;

  @NotBlank
  private String institutionId;


  private String cursor;

  @Range(min = 1, max = 20)
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
