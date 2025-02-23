package org.example.gongiklifeclientbegraphql.service.institution;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.MyInstitutionReviewsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.institution.myInstitutionReviews.MyInstitutionReviewsResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyInstitutionReviewsService {

  @GrpcClient("gongik-life-client-be-institution-service")
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

  public MyInstitutionReviewsResponseDto myInstitutionReviews(String userId) {

    return ServiceExceptionHandlingUtil.handle("myInstitutionReviews", () -> {
      return MyInstitutionReviewsResponseDto.fromProto(
          institutionBlockingStub.myInstitutionReviews(
              MyInstitutionReviewsRequest.newBuilder()
                  .setUserId(userId)
                  .build()
          )
      );
    });
  }
}
