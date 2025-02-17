package org.example.gongiklifeclientbegraphql.dto.workhours.averageWorkhours;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AverageWorkhoursResponseDto {

  private Integer myAverageWorkhours;
  private Integer socialWelfareWorkhours;
  private Integer publicOrganizationWorkhours;
  private Integer nationalAgencyWorkhours;
  private Integer localGovernmentWorkhours;
  private Integer totalVoteCount;

}
