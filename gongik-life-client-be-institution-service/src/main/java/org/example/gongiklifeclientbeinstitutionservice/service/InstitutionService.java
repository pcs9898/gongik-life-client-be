package org.example.gongiklifeclientbeinstitutionservice.service;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.CreateInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.PageInfo;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsResponse;
import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.CheckUserInstitutionRequest;
import io.grpc.Status;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbeinstitutionservice.document.InstitutionDocument;
import org.example.gongiklifeclientbeinstitutionservice.entity.Institution;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReview;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionDiseaseRestrictionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.elasticsearch.InstitutionSearchRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InstitutionService {

  private final InstitutionSearchRepository institutionSearchRepository;
  private final InstitutionRepository institutionRepository;
  private final InstitutionDiseaseRestrictionRepository institutionDiseaseRestrictionRepository;
  private final InstitutionReviewRepository institutionReviewRepository;
  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

  public SearchInstitutionsResponse searchInstitutions(
      SearchInstitutionsRequest request) {
    List<InstitutionDocument> institutions;
    if (request.getCursor().isEmpty()) {
      institutions = institutionSearchRepository.findByNameContainingOrderByIdAsc(
          request.getSearchKeyword(), Pageable.ofSize(request.getPageSize()));
    } else {
      institutions = institutionSearchRepository.findByNameContainingAndIdGreaterThanOrderByIdAsc(
          request.getSearchKeyword(), request.getCursor(),
          Pageable.ofSize(request.getPageSize()));
    }

    SearchInstitutionsResponse.Builder responseBuilder = SearchInstitutionsResponse.newBuilder();
    for (InstitutionDocument institution : institutions) {
      responseBuilder.addListSearchInstitution(institution.toProto());
    }

    String endCursor =
        institutions.isEmpty() ? "" : institutions.get(institutions.size() - 1).getId();
    boolean hasNextPage = institutions.size() == request.getPageSize();

    responseBuilder.setPageInfo(PageInfo.newBuilder()
        .setEndCursor(endCursor)
        .setHasNextPage(hasNextPage)
        .build());

    return responseBuilder.build();
  }

  public GetInstitutionNameResponse getInstitutionName(GetInstitutionNameRequest request) {

    String institutionId = request.getId();
    log.info("getInstitutionName request : {}",
        (institutionId != null && !institutionId.isEmpty()) ? institutionId : "null");
    Institution institution = institutionRepository.findById(UUID.fromString(request.getId()))
        .orElseThrow(() -> new IllegalArgumentException("Institution not found"));

    return GetInstitutionNameResponse.newBuilder()
        .setName(institution.getName())
        .build();
  }

  @Transactional(readOnly = true)
  public InstitutionResponse institution(InstitutionRequest request) {
    Institution institution = institutionRepository.findById(
            UUID.fromString(request.getInstitutionId()))
        .orElseThrow(() -> new NotFoundException("Institution not found, wrong institution id"));

    List<Integer> diseaseids = institutionDiseaseRestrictionRepository.findByInstitutionId(
            institution.getId()).stream()
        .map(a -> {
          return a.getDiseaseRestriction().getId();
        })
        .collect(Collectors.toList());

    log.info("diseaseids : {}", diseaseids);

    InstitutionResponse.Builder response = institution.toInstitutionResponseProto();

    response.addAllDiseaseRestrictions(diseaseids);
    return response.build();

  }

  @Transactional
  public InstitutionReviewResponse createInstitutionReview(CreateInstitutionReviewRequest request) {
    Institution institution = institutionRepository.findById(
            UUID.fromString(request.getInstitutionId()))
        .orElseThrow(() ->

            Status.INVALID_ARGUMENT
                .withDescription("Institution not found, wrong institution id")
                .asRuntimeException()
        );

    // 1. 내 프로필 정보가져와서 기관id있는지 확인 grpc
    String userName = userServiceBlockingStub.checkUserInstitution(
        CheckUserInstitutionRequest.newBuilder().setUserId(request.getUserId())
            .setInstitutionId(request.getInstitutionId()).build()).getUserName();

    if (userName.isEmpty()) {
      throw Status.INVALID_ARGUMENT
          .withDescription("User and institution does not match")
          .asRuntimeException();
    }

    // 2. 있다면 내가 앞서 동일한 기관에 대한 리뷰를 작성했는지 확인 repository
    boolean existsByUserIdAndInstitutionId = institutionReviewRepository
        .existsByUserIdAndInstitutionId(UUID.fromString(request.getUserId()),
            UUID.fromString(request.getInstitutionId()));

    if (existsByUserIdAndInstitutionId) {
      throw Status.INVALID_ARGUMENT
          .withDescription("User already reviewed this institution")
          .asRuntimeException();

    }

    // 3. 없다면 리뷰 생성 respository
    Double rating = (double) (request.getFacilityRating() + request.getLocationRating()
        + request.getStaffRating() + request.getVisitorRating()
        + request.getVacationFreedomRating())
        / 5;

    institution.setReviewCount(institution.getReviewCount() + 1);
    institutionRepository.save(institution);

    InstitutionReviewResponse response = institutionReviewRepository
        .save(InstitutionReview.fromProto(request, institution, rating)).toProto(userName);
    log.info("response : {}", response);
    return response;


  }


  @Transactional
  public DeleteInstitutionReviewResponse deleteInstitutionReview(
      DeleteInstitutionReviewRequest request) {
    InstitutionReview institutionReview = institutionReviewRepository.findById(
            UUID.fromString(request.getInstitutionReviewId()))
        .orElseThrow(() -> Status.NOT_FOUND
            .withDescription("Institution review not found, wrong institution review id")
            .asRuntimeException()

        );

    if (!institutionReview.getUserId().equals(UUID.fromString(request.getUserId()))) {
      throw Status.PERMISSION_DENIED
          .withDescription("You can delete only your institution review")
          .asRuntimeException();
    }

    Institution institution = institutionRepository.findById(
            institutionReview.getInstitution().getId())
        .orElseThrow(() ->
            Status.NOT_FOUND
                .withDescription("Institution not found, wrong institution id")
                .asRuntimeException()
        );

    institution.setReviewCount(institution.getReviewCount() - 1);
    institutionRepository.save(institution);

    institutionReviewRepository.delete(institutionReview);

    return DeleteInstitutionReviewResponse.newBuilder().setSuccess(true).build();
  }
}