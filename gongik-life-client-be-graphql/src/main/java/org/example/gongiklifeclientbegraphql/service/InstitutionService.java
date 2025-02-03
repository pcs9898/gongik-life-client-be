package org.example.gongiklifeclientbegraphql.service;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsResponse;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.institution.InstitutionRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.InstitutionResponseDto;
import org.example.gongiklifeclientbegraphql.dto.searchInstitutions.SearchInstitutionsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.searchInstitutions.SearchInstitutionsResultsDto;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InstitutionService {

  @GrpcClient("gongik-life-client-be-institution-service")
  private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

  public SearchInstitutionsResultsDto searchInstitutions(SearchInstitutionsRequestDto requestDto) {
    try {
      SearchInstitutionsResponse response = institutionBlockingStub.searchInstitutions(
          requestDto.toProto());

      return SearchInstitutionsResultsDto.fromProto(response);
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  public InstitutionResponseDto institution(InstitutionRequestDto requestDto) {
    try {
      return InstitutionResponseDto.fromProto(
          institutionBlockingStub.institution(requestDto.toProto()));
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }
}