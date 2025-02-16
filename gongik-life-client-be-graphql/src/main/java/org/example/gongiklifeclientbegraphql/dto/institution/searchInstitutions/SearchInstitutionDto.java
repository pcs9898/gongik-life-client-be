package org.example.gongiklifeclientbegraphql.dto.institution.searchInstitutions;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitution;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class SearchInstitutionDto {

  private String id;
  private String name;
  private String address;
  private Float averageRating;

  public static SearchInstitutionDto fromProto(SearchInstitution proto) {
    log.info("fromProto: {}", proto.getAverageRating());
    SearchInstitutionDto dto = new SearchInstitutionDto();
    dto.setId(proto.getId());
    dto.setName(proto.getName());
    dto.setAddress(proto.getAddress());
    dto.setAverageRating(proto.getAverageRating());
    return dto;
  }
}