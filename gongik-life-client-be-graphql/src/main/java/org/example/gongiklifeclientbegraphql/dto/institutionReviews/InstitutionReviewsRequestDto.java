package org.example.gongiklifeclientbegraphql.dto.institutionReviews;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionReviewsRequestDto {


  private String userId;
  private int institutionCategoryId;
  private String cursor;
  private int pageSize;

  public InstitutionServiceOuterClass.InstitutionReviewsRequest toProto() {
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
