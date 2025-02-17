package org.example.gongiklifeclientbeworkhoursservice.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionDto {

  private UUID id;
  private int institutionCategoryId; // 1,2,3,4
  private int averageWorkhours;
  private int reviewCount;
}