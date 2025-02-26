package org.example.gongiklifeclientbeinstitutionservice.service;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.ExistsInstitutionRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.ExistsInstitutionResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.ExistsInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.ExistsInstitutionReviewResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionReviewCountRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionReviewCountResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetMyAverageWorkhoursRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetMyAverageWorkhoursResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.IsLikedInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.IsLikedInstitutionReviewResponse;
import com.gongik.userService.domain.service.UserServiceGrpc;
import io.grpc.Status;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbeinstitutionservice.dto.InstitutionForWorkHoursStatisticsProjection;
import org.example.gongiklifeclientbeinstitutionservice.entity.Institution;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReview;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionDiseaseRestrictionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewLikeRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.elasticsearch.InstitutionSearchRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InstitutionService {

  private final InstitutionSearchRepository institutionSearchRepository;
  private final InstitutionRepository institutionRepository;
  private final InstitutionDiseaseRestrictionRepository institutionDiseaseRestrictionRepository;
  private final InstitutionReviewRepository institutionReviewRepository;
  private final InstitutionReviewLikeRepository institutionReviewLikeRepository;

  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;


  public GetInstitutionNameResponse getInstitutionName(GetInstitutionNameRequest request) {

    Institution institution = institutionRepository.findById(UUID.fromString(request.getId()))
        .orElseThrow(() -> new IllegalArgumentException("Institution not found"));

    return GetInstitutionNameResponse.newBuilder()
        .setName(institution.getName())
        .build();
  }

  public IsLikedInstitutionReviewResponse isLikedInstitutionReview(
      IsLikedInstitutionReviewRequest request) {

    try {
      boolean existsByUserIdAndInstitutionReviewId = institutionReviewLikeRepository
          .existsByIdInstitutionReviewIdAndIdUserId(
              UUID.fromString(request.getInstitutionReviewId()),
              UUID.fromString(request.getUserId()));

      return IsLikedInstitutionReviewResponse.newBuilder()
          .setIsLiked(existsByUserIdAndInstitutionReviewId)
          .build();
    } catch (IllegalArgumentException e) {

      return IsLikedInstitutionReviewResponse.newBuilder()
          .setIsLiked(false)
          .build();
    }

  }


  public GetInstitutionReviewCountResponse getInstitutionReviewCount(
      GetInstitutionReviewCountRequest request) {
    int count = institutionRepository.getReviewCount(
        UUID.fromString(request.getInstitutionId()));

    return GetInstitutionReviewCountResponse.newBuilder()
        .setReviewCount(count)
        .build();
  }

  public ExistsInstitutionResponse existsInstitution(ExistsInstitutionRequest request) {

    boolean exists = institutionRepository.existsById(UUID.fromString(request.getInstitutionId()));

    return ExistsInstitutionResponse.newBuilder()
        .setExists(exists)
        .build();
  }

  public ExistsInstitutionReviewResponse existsInstitutionReview(
      ExistsInstitutionReviewRequest request) {

    boolean exists = institutionReviewRepository.existsById(
        UUID.fromString(request.getInstitutionReviewId()));

    return ExistsInstitutionReviewResponse.newBuilder()
        .setExists(exists)
        .build();
  }

  public List<InstitutionForWorkHoursStatisticsProjection> getInstitutionsForWorkHourStatistics() {
    return institutionRepository.findAllInstitutionsForWorkHoursStatistics();
  }

  public GetMyAverageWorkhoursResponse getMyAverageWorkhours(GetMyAverageWorkhoursRequest request) {
    Institution institution = institutionRepository.findById(
            UUID.fromString(request.getInstitutionId()))
        .orElseThrow(() -> Status.NOT_FOUND
            .withDescription("Institution not found, wrong institution id")
            .asRuntimeException()
        );

    InstitutionReview institutionReview = institutionReviewRepository.findByUserIdAndInstitution(
            UUID.fromString(request.getUserId()), institution)
        .orElseThrow(() -> Status.NOT_FOUND
            .withDescription(
                "Institution review not found, if you want to get your average workhours, you should write a review first")
            .asRuntimeException()
        );

    return GetMyAverageWorkhoursResponse.newBuilder()
        .setMyAverageWorkhours(institutionReview.getAverageWorkhours()).build();


  }
}