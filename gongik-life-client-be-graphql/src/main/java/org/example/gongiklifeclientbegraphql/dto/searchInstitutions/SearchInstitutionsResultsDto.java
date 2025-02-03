package org.example.gongiklifeclientbegraphql.dto.searchInstitutions;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import org.example.gongiklifeclientbegraphql.dto.common.PageInfoDto;

@Data
public class SearchInstitutionsResultsDto {

  private List<SearchInstitutionDto> listSearchInstitution;
  private PageInfoDto pageInfo;

  public static SearchInstitutionsResultsDto fromProto(SearchInstitutionsResponse proto) {
    SearchInstitutionsResultsDto dto = new SearchInstitutionsResultsDto();
    dto.setListSearchInstitution(proto.getListSearchInstitutionList().stream()
        .map(SearchInstitutionDto::fromProto)
        .collect(Collectors.toList()));
    dto.setPageInfo(PageInfoDto.fromProto(proto.getPageInfo()));
    return dto;
  }
}