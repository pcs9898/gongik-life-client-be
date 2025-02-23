package org.example.gongiklifeclientbegraphql.service.institution;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviews.InstitutionReviewsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviews.InstitutionReviewsResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetInstitutionReviewsService {

  @GrpcClient("gongik-life-client-be-institution-service")
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

  public InstitutionReviewsResponseDto institutionReviews(InstitutionReviewsRequestDto requestDto) {

    return ServiceExceptionHandlingUtil.handle("institutionReviews", () -> {
      return InstitutionReviewsResponseDto.fromInstitutionReviewsResponseProto(
          institutionBlockingStub.institutionReviews(
              requestDto.toInstitutionReviewsRequestProto()));
    });
  }
}
