package org.example.gongiklifeclientbegraphql.service.institution;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionReviewCountRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.institution.institution.InstitutionRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institution.InstitutionResponseDto;
import org.example.gongiklifeclientbegraphql.service.InstitutionCacheService;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetInstitutionService {

  private final InstitutionCacheService institutionCacheService;

  @GrpcClient("gongik-life-client-be-institution-service")
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;


  public InstitutionResponseDto institution(InstitutionRequestDto requestDto) {
    return ServiceExceptionHandlingUtil.handle("GetInstitutionService", () -> {

      InstitutionResponseDto institutionResponseDto = institutionCacheService.getInstitution(
          requestDto.getInstitutionId());
      Integer reviewCount = institutionBlockingStub.getInstitutionReviewCount(
          GetInstitutionReviewCountRequest.newBuilder()
              .setInstitutionId(requestDto.getInstitutionId())
              .build()
      ).getReviewCount();
      institutionResponseDto.setReviewCount(reviewCount);
      return institutionResponseDto;
    });
  }
}
