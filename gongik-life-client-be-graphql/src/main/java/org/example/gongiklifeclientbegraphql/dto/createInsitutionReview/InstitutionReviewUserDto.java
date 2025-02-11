package org.example.gongiklifeclientbegraphql.dto.createInsitutionReview;

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
}