package org.example.gongiklifeclientbegraphql.service;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.institution.institution.InstitutionResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReview.InstitutionReviewResponseDto;
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
    try {
      return InstitutionReviewResponseDto.fromProto(
          institutionBlockingStub.institutionReview(
              InstitutionReviewRequest.newBuilder().setInstitutionReviewId(institutionReviewId)
                  .build()
          ));
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  @Cacheable(value = "institution", key = "#institutionId")
  public InstitutionResponseDto getInstitution(String institutionId) {
    try {
      return InstitutionResponseDto.fromProto(
          institutionBlockingStub.institution(
              InstitutionRequest.newBuilder().setInstitutionId(institutionId)
                  .build()
          ));
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }
}
