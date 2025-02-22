package org.example.gongiklifeclientbegraphql.service.institution;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.institution.searchInstitutions.SearchInstitutionsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.searchInstitutions.SearchInstitutionsResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchInstitutionsService {

  @GrpcClient("gongik-life-client-be-institution-service")
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

  public SearchInstitutionsResponseDto searchInstitutions(SearchInstitutionsRequestDto requestDto) {

    return ServiceExceptionHandlingUtil.handle("SearchInstitutionsService", () -> {

      Assert.notNull(requestDto, "requestDto must not be null");

      return SearchInstitutionsResponseDto.fromSearchInstitutionsResponseProto(
          institutionBlockingStub.searchInstitutions(
              requestDto.toSearchInstitutionsRequestProto())
      );
    });
  }
}
