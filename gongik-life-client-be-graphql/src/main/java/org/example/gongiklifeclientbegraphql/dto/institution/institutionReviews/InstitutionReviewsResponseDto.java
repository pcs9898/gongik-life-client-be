package org.example.gongiklifeclientbegraphql.dto.institution.institutionReviews;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.common.InstitutionReviewForListDto;
import org.example.gongiklifeclientbegraphql.dto.common.PageInfoDto;

@Data
@Slf4j
public class InstitutionReviewsResponseDto {

  private List<InstitutionReviewForListDto> listInstitutionReview;
  private PageInfoDto pageInfo;

  public static InstitutionReviewsResponseDto fromInstitutionReviewsResponseProto(
      InstitutionReviewsResponse proto) {
    InstitutionReviewsResponseDto dto = new InstitutionReviewsResponseDto();
    dto.setListInstitutionReview(proto.getListInstitutionReviewList().stream()
        .map(InstitutionReviewForListDto::fromProto)
        .collect(Collectors.toList()));
    dto.setPageInfo(PageInfoDto.fromProto(proto.getPageInfo()));

    log.info("InstitutionReviewsResponseDto : {}", dto);
    return dto;
  }


}
