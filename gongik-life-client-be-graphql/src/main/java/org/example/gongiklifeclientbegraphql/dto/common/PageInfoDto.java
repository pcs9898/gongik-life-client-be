package org.example.gongiklifeclientbegraphql.dto.common;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageInfoDto {

  private String endCursor;
  private boolean hasNextPage;

  public static PageInfoDto fromProto(PageInfo proto) {
    PageInfoDto dto = new PageInfoDto();
    dto.setEndCursor(proto.getEndCursor());
    dto.setHasNextPage(proto.getHasNextPage());
    return dto;
  }
}