package org.example.gongiklifeclientbegraphql.service;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.IsLikedInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.MyInstitutionReviewsRequest;
import dto.institution.LikeInstitutionReviewRequestDto;
import dto.institution.UnlikeInstitutionReviewRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.institution.createInsitutionReview.CreateInstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.createInsitutionReview.CreateInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.deleteInstitutionReview.DeleteInstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.deleteInstitutionReview.DeleteInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReview.InstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReview.InstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviews.InstitutionReviewsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviews.InstitutionReviewsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviewsByInstitution.InstitutionReviewsByInstitutionRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviewsByInstitution.InstitutionReviewsByInstitutionResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.likeInstitutionReview.LikeInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.myInstitutionReviews.MyInstitutionReviewsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.institution.unlikeInstitutionReview.UnlikeInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.producer.institution.LikeInstitutionReviewProducer;
import org.example.gongiklifeclientbegraphql.producer.institution.UnlikeInstitutionReviewProducer;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
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

  public CreateInstitutionReviewResponseDto createInstitutionReview(
      CreateInstitutionReviewRequestDto requestDto) {

    return ServiceExceptionHandlingUtil.handle("createInstitutionReview", () -> {
      return CreateInstitutionReviewResponseDto.fromProto(
          institutionBlockingStub.createInstitutionReview(requestDto.toProto()));
    });
  }

  @CacheEvict(value = "institutionReview", key = "#requestDto.getInstitutionReviewId()")
  public DeleteInstitutionReviewResponseDto deleteInstitutionReview(
      DeleteInstitutionReviewRequestDto requestDto) {

    return ServiceExceptionHandlingUtil.handle("deleteInstitutionReview", () -> {
      DeleteInstitutionReviewResponse result = institutionBlockingStub.deleteInstitutionReview(
          requestDto.toProto());

      if (!result.getSuccess()) {
        throw new RuntimeException("Failed to delete institution review");
      }

      return DeleteInstitutionReviewResponseDto.fromProto(
          requestDto.getInstitutionReviewId()
      );
    });
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

    return ServiceExceptionHandlingUtil.handle("institutionReview", () -> {
      InstitutionReviewResponseDto institutionReview = institutionCacheService.getInstitutionReview(
          requestDto.getInstitutionReviewId());

      if (requestDto.getUserId() != null) {

        boolean isLiked = isLikedInstitutionReview(requestDto.getInstitutionReviewId(),
            requestDto.getUserId());
        institutionReview.setIsLiked(isLiked);
      }

      return institutionReview;
    });
  }


  public Boolean isLikedInstitutionReview(String institutionReviewId, String userId) {

    return ServiceExceptionHandlingUtil.handle("isLikedInstitutionReview", () -> {
      return institutionBlockingStub.isLikedInstitutionReview(
          IsLikedInstitutionReviewRequest.newBuilder()
              .setInstitutionReviewId(institutionReviewId)
              .setUserId(userId)
              .build()
      ).getIsLiked();
    });
  }

  public InstitutionReviewsResponseDto institutionReviews(InstitutionReviewsRequestDto requestDto) {
    return ServiceExceptionHandlingUtil.handle("institutionReviews", () -> {
      return InstitutionReviewsResponseDto.fromProto(
          institutionBlockingStub.institutionReviews(requestDto.toProto()));
    });
  }

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

  public InstitutionReviewsByInstitutionResponseDto institutionReviewsByInstitution(
      InstitutionReviewsByInstitutionRequestDto requestDto) {

    return ServiceExceptionHandlingUtil.handle("institutionReviewsByInstitution", () -> {
      return InstitutionReviewsByInstitutionResponseDto.fromProto(
          institutionBlockingStub.institutionReviewsByInstitution(requestDto.toProto()));
    });
  }

  public Integer getMyAverageWorkhours(String userId, String userInstitutionId) {

    return ServiceExceptionHandlingUtil.handle("getMyAverageWorkhours", () -> {
      return institutionBlockingStub.getMyAverageWorkhours(
          com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetMyAverageWorkhoursRequest.newBuilder()
              .setUserId(userId)
              .setInstitutionId(userInstitutionId)
              .build()
      ).getMyAverageWorkhours();
    });
  }
}