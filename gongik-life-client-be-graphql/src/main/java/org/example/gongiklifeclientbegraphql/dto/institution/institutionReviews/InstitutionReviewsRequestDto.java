package org.example.gongiklifeclientbegraphql.dto.institution.institutionReviews;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionReviewsRequestDto {


  private String userId;

  @Range(min = 1, max = 7)  //7 is all
  private int institutionCategoryId;

  private String cursor;

  @Range(min = 1, max = 20)
  private int pageSize;

  public InstitutionServiceOuterClass.InstitutionReviewsRequest toInstitutionReviewsRequestProto() {
    InstitutionServiceOuterClass.InstitutionReviewsRequest.Builder builder = InstitutionServiceOuterClass.InstitutionReviewsRequest.newBuilder()
        .setUserId(userId)
        .setInstitutionCategoryId(institutionCategoryId)
        .setPageSize(pageSize);

    if (cursor != null) {
      builder.setCursor(cursor);
    }

    return builder.build();
  }
}
