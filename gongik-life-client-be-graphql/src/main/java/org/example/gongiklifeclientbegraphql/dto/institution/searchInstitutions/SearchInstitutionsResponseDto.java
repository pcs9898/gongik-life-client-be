package org.example.gongiklifeclientbegraphql.dto.institution.searchInstitutions;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.common.PageInfoDto;

@Data
@Slf4j
public class SearchInstitutionsResponseDto {

  private List<SearchInstitutionDto> listSearchInstitution;
  private PageInfoDto pageInfo;

  public static SearchInstitutionsResponseDto fromSearchInstitutionsResponseProto(
      SearchInstitutionsResponse proto) {
    SearchInstitutionsResponseDto dto = new SearchInstitutionsResponseDto();
    dto.setListSearchInstitution(proto.getListSearchInstitutionList().stream()
        .map(SearchInstitutionDto::fromProto)
        .collect(Collectors.toList()));
    dto.setPageInfo(PageInfoDto.fromProto(proto.getPageInfo()));

    return dto;
  }
}