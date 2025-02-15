package org.example.gongiklifeclientbegraphql.dto.institutionReviewsByInstitution;


import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsByInstitutionResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.common.InstitutionReviewForListDto;
import org.example.gongiklifeclientbegraphql.dto.common.PageInfoDto;

@Data
@Slf4j
public class InstitutionReviewsByInstitutionResponseDto {

  private List<InstitutionReviewForListDto> listInstitutionReviewByInstitution;
  private PageInfoDto pageInfo;

  public static InstitutionReviewsByInstitutionResponseDto fromProto(
      InstitutionReviewsByInstitutionResponse proto) {
    InstitutionReviewsByInstitutionResponseDto dto = new InstitutionReviewsByInstitutionResponseDto();
    dto.setListInstitutionReviewByInstitution(
        proto.getListInstitutionReviewByInstitutionList().stream()
            .map(InstitutionReviewForListDto::fromProto)
            .collect(Collectors.toList()));
    dto.setPageInfo(PageInfoDto.fromProto(proto.getPageInfo()));

    log.info("InstitutionReviewsByInstitutionResponseDto : {}", dto);
    return dto;
  }


}
