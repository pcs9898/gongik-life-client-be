package org.example.gongiklifeclientbegraphql.dto.myInstitutionReviews;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.MyInstitutionReviewsResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import org.example.gongiklifeclientbegraphql.dto.common.InstitutionReviewForListDto;

@Data
public class MyInstitutionReviewsResponseDto {

  private List<InstitutionReviewForListDto> listMyInstitutionReview;

  public static MyInstitutionReviewsResponseDto fromProto(MyInstitutionReviewsResponse proto) {
    MyInstitutionReviewsResponseDto dto = new MyInstitutionReviewsResponseDto();
    dto.setListMyInstitutionReview(proto.getListMyInstitutionReviewList().stream()
        .map(InstitutionReviewForListDto::fromProto)
        .collect(Collectors.toList()));
    return dto;
  }

}
