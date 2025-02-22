package org.example.gongiklifeclientbegraphql.dto.institution.searchInstitutions;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchInstitutionsRequestDto {

  @NotBlank
  private String searchKeyword;

  private String cursor;

  @NotNull
  @Range(min = 1, max = 20)
  private Integer pageSize;

  public SearchInstitutionsRequest toSearchInstitutionsRequestProto() {
    SearchInstitutionsRequest.Builder builder = SearchInstitutionsRequest.newBuilder()
        .setSearchKeyword(this.searchKeyword)
        .setPageSize(this.pageSize);

    if (this.cursor != null) {
      builder.setCursor(this.cursor);
    }

    return builder.build();
  }
}