package org.example.gongiklifeclientbegraphql.dto.institutionReview;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionReviewUserDto {

  private String id;
  private String name;

  public static InstitutionReviewUserDto fromProto(InstitutionReviewUser user) {
    return InstitutionReviewUserDto.builder()
        .id(user.getId())
        .name(user.getName())
        .build();
  }
}