package org.example.gongiklifeclientbegraphql.service;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionReviewCountRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.IsLikedInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.MyInstitutionReviewsRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsResponse;
import dto.institution.LikeInstitutionReviewRequestDto;
import dto.institution.UnlikeInstitutionReviewRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.institution.createInsitutionReview.CreateInstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.createInsitutionReview.CreateInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.deleteInstitutionReview.DeleteInstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.deleteInstitutionReview.DeleteInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institution.InstitutionRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institution.InstitutionResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReview.InstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReview.InstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviews.InstitutionReviewsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviews.InstitutionReviewsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviewsByInstitution.InstitutionReviewsByInstitutionRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviewsByInstitution.InstitutionReviewsByInstitutionResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.likeInstitutionReview.LikeInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.myInstitutionReviews.MyInstitutionReviewsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.searchInstitutions.SearchInstitutionsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.searchInstitutions.SearchInstitutionsResultsDto;
import org.example.gongiklifeclientbegraphql.dto.institution.unlikeInstitutionReview.UnlikeInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.producer.institution.LikeInstitutionReviewProducer;
import org.example.gongiklifeclientbegraphql.producer.institution.UnlikeInstitutionReviewProducer;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InstitutionService {

  private final LikeInstitutionReviewProducer likeInstitutionReviewProducer;
  private final UnlikeInstitutionReviewProducer unlikeInstitutionReviewProducer;
  private final InstitutionCacheService institutionCacheService;

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
      InstitutionResponseDto institutionResponseDto = institutionCacheService.getInstitution(
          requestDto.getInstitutionId());

      Integer reviewCount = institutionBlockingStub.getInstitutionReviewCount(
          GetInstitutionReviewCountRequest.newBuilder()
              .setInstitutionId(requestDto.getInstitutionId()).build()
      ).getReviewCount();

      institutionResponseDto.setReviewCount(reviewCount);

      return institutionResponseDto;
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  public CreateInstitutionReviewResponseDto createInstitutionReview(
      CreateInstitutionReviewRequestDto requestDto) {
    try {
      return CreateInstitutionReviewResponseDto.fromProto(
          institutionBlockingStub.createInstitutionReview(requestDto.toProto()));
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  @CacheEvict(value = "institutionReview", key = "#requestDto.getInstitutionReviewId()")
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

  public UnlikeInstitutionReviewResponseDto unlikeInstitutionReview(
      UnlikeInstitutionReviewRequestDto requestDto) {

    unlikeInstitutionReviewProducer.sendUnlikeInstitutionReviewRequest(requestDto);

    return UnlikeInstitutionReviewResponseDto.builder()
        .success(true)
        .build();
  }


  public InstitutionReviewResponseDto institutionReview(InstitutionReviewRequestDto requestDto) {
    InstitutionReviewResponseDto institutionReview = institutionCacheService.getInstitutionReview(
        requestDto.getInstitutionReviewId());

    if (requestDto.getUserId() != null) {

      boolean isLiked = isLikedInstitutionReview(requestDto.getInstitutionReviewId(),
          requestDto.getUserId());
      institutionReview.setIsLiked(isLiked);
    }

    return institutionReview;
  }


  public Boolean isLikedInstitutionReview(String institutionReviewId, String userId) {
    try {
      Boolean response = institutionBlockingStub.isLikedInstitutionReview(
          IsLikedInstitutionReviewRequest.newBuilder()
              .setInstitutionReviewId(institutionReviewId)
              .setUserId(userId)
              .build()
      ).getIsLiked();

      log.info("isLikedInstitutionReview response: {}", response);
      return response;
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  public InstitutionReviewsResponseDto institutionReviews(InstitutionReviewsRequestDto requestDto) {
    try {
      InstitutionReviewsResponse response = institutionBlockingStub.institutionReviews(
          requestDto.toProto());

      log.info("institutionReviews response: {}", response.getListInstitutionReviewList());

      return InstitutionReviewsResponseDto.fromProto(
          institutionBlockingStub.institutionReviews(requestDto.toProto()));
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  public MyInstitutionReviewsResponseDto myInstitutionReviews(String userId) {

    try {
      return MyInstitutionReviewsResponseDto.fromProto(
          institutionBlockingStub.myInstitutionReviews(
              MyInstitutionReviewsRequest.newBuilder()
                  .setUserId(userId)
                  .build()
          )
      );
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  public InstitutionReviewsByInstitutionResponseDto institutionReviewsByInstitution(
      InstitutionReviewsByInstitutionRequestDto requestDto) {
    try {
      return InstitutionReviewsByInstitutionResponseDto.fromProto(
          institutionBlockingStub.institutionReviewsByInstitution(requestDto.toProto()));
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;

    }
  }

  public Integer getMyAverageWorkhours(String userId, String userInstitutionId) {

    try {
      return institutionBlockingStub.getMyAverageWorkhours(
          com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetMyAverageWorkhoursRequest.newBuilder()
              .setUserId(userId)
              .setInstitutionId(userInstitutionId)
              .build()
      ).getMyAverageWorkhours();
    } catch (Exception e) {
      log.error("graphql -> institutionService -> getMyAverageWorkhours error", e);
      throw e;
    }
  }
}