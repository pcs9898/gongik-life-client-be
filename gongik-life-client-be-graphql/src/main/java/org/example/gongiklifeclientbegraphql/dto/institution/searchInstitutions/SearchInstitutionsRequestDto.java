package org.example.gongiklifeclientbegraphql.dto.institution.searchInstitutions;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsRequest;
import lombok.Data;

@Data
public class SearchInstitutionsRequestDto {

  private String searchKeyword;

  private String cursor;
  private int pageSize;

  public SearchInstitutionsRequest toProto() {
    SearchInstitutionsRequest.Builder builder = SearchInstitutionsRequest.newBuilder()
        .setSearchKeyword(this.searchKeyword)
        .setPageSize(this.pageSize);

    if (this.cursor != null) {
      builder.setCursor(this.cursor);
    }

    return builder.build();
  }
}