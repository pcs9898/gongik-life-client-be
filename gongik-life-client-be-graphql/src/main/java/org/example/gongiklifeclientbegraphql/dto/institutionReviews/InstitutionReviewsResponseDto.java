package org.example.gongiklifeclientbegraphql.dto.institutionReviews;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import org.example.gongiklifeclientbegraphql.dto.common.PageInfoDto;

@Data
public class InstitutionReviewsResponseDto {

  private List<InstitutionReviewForListDto> listInstitutionReview;
  private PageInfoDto pageInfo;

  public static InstitutionReviewsResponseDto fromProto(InstitutionReviewsResponse proto) {
    InstitutionReviewsResponseDto dto = new InstitutionReviewsResponseDto();
    dto.setListInstitutionReview(proto.getListInstitutionReviewList().stream()
        .map(InstitutionReviewForListDto::fromProto)
        .collect(Collectors.toList()));
    dto.setPageInfo(PageInfoDto.fromProto(proto.getPageInfo()));
    return dto;
  }


}
