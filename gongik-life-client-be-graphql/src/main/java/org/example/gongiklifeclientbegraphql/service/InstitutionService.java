package org.example.gongiklifeclientbegraphql.service;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsResponse;
import dto.institution.LikeInstitutionReviewRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.createInsitutionReview.CreateInstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.createInsitutionReview.InstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.deleteInstitutionReview.DeleteInstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.deleteInstitutionReview.DeleteInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.InstitutionRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.InstitutionResponseDto;
import org.example.gongiklifeclientbegraphql.dto.likeInstitutionReview.LikeInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.searchInstitutions.SearchInstitutionsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.searchInstitutions.SearchInstitutionsResultsDto;
import org.example.gongiklifeclientbegraphql.producer.LikeInstitutionReviewProducer;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InstitutionService {

  private final LikeInstitutionReviewProducer likeInstitutionReviewProducer;
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

  public InstitutionReviewResponseDto createInstitutionReview(
      CreateInstitutionReviewRequestDto requestDto) {
    try {
      return InstitutionReviewResponseDto.fromProto(
          institutionBlockingStub.createInstitutionReview(requestDto.toProto()));
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  public DeleteInstitutionReviewResponseDto deleteInstitutionReview(
      DeleteInstitutionReviewRequestDto requestDto) {
    try {
      DeleteInstitutionReviewResponse result = institutionBlockingStub.deleteInstitutionReview(
          requestDto.toProto());

      if (!result.getSuccess()) {
        throw new RuntimeException("Failed to delete institution review");
      }

      return DeleteInstitutionReviewResponseDto.fromProto(
          requestDto.getInstitutionReviewId()
      );

    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  public LikeInstitutionReviewResponseDto likeInstitutionReview(
      LikeInstitutionReviewRequestDto requestDto) {

    likeInstitutionReviewProducer.sendLikeInstitutionReviewRequest(requestDto);

    return LikeInstitutionReviewResponseDto.builder()
        .success(true)
        .build();
  }
}