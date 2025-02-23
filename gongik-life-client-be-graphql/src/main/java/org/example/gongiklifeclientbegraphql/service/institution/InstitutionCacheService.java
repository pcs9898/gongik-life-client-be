package org.example.gongiklifeclientbegraphql.service.institution;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.institution.institution.InstitutionResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReview.InstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class InstitutionCacheService {

  @GrpcClient("gongik-life-client-be-institution-service")
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

  @Cacheable(value = "institutionReview", key = "#institutionReviewId")
  public InstitutionReviewResponseDto getInstitutionReview(String institutionReviewId) {

    return ServiceExceptionHandlingUtil.handle("institutionReview", () -> {
      return InstitutionReviewResponseDto.fromInstitutionReviewResponseProto(
          institutionBlockingStub.institutionReview(
              InstitutionReviewRequest.newBuilder().setInstitutionReviewId(institutionReviewId)
                  .build()
          ));
    });
  }

  @Cacheable(value = "institution", key = "#institutionId")
  public InstitutionResponseDto getInstitution(String institutionId) {

    return ServiceExceptionHandlingUtil.handle("institution", () -> {
      return InstitutionResponseDto.fromInstitutionResponseProto(
          institutionBlockingStub.institution(
              InstitutionRequest.newBuilder().setInstitutionId(institutionId)
                  .build()
          ));
    });

  }
}

