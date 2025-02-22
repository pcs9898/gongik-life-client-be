package org.example.gongiklifeclientbeinstitutionservice.service;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.CreateInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewResponse;
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
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewForList;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewInstitution;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewUser;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsByInstitutionRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsByInstitutionResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.IsLikedInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.IsLikedInstitutionReviewResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.MyInstitutionReviewsRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.MyInstitutionReviewsResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.PageInfo;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitution;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsResponse;
import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.CheckUserInstitutionRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdsRequest;
import dto.institution.LikeInstitutionReviewRequestDto;
import dto.institution.UnlikeInstitutionReviewRequestDto;
import io.grpc.Status;
import jakarta.ws.rs.NotFoundException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbeinstitutionservice.dto.InstitutionForWorkHoursStatisticsProjection;
import org.example.gongiklifeclientbeinstitutionservice.dto.InstitutionReviewProjection;
import org.example.gongiklifeclientbeinstitutionservice.dto.InstitutionSimpleProjection;
import org.example.gongiklifeclientbeinstitutionservice.entity.Institution;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReview;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReviewLike;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReviewLikeId;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionDiseaseRestrictionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewLikeRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.elasticsearch.InstitutionSearchRepository;
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
  private final InstitutionReviewLikeRepository institutionReviewLikeRepository;

  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

  public SearchInstitutionsResponse searchInstitutions(
      SearchInstitutionsRequest request) {

    List<InstitutionSimpleProjection> institutions = institutionRepository.searchInstitutions(
        request.getSearchKeyword(),
        request.getCursor().isEmpty() ? null : UUID.fromString(request.getCursor()),
        request.getPageSize()
    );

    List<SearchInstitution> listSearchInstitution = institutions.stream()
        .map(institution -> {
          return SearchInstitution.newBuilder()
              .setId(institution.getId().toString())
              .setName(institution.getName())
              .setAddress(institution.getAddress())
              .setAverageRating(
                  institution.getAverageRating() != null ? institution.getAverageRating()
                      .floatValue() : 0.0f)
              .build();
        })
        .toList();

    SearchInstitutionsResponse.Builder responseBuilder = SearchInstitutionsResponse.newBuilder()
        .addAllListSearchInstitution(listSearchInstitution);

    String endCursor =
        institutions.isEmpty() ? "" : institutions.get(institutions.size() - 1).getId().toString();
    boolean hasNextPage = institutions.size() == request.getPageSize();

    responseBuilder.setPageInfo(PageInfo.newBuilder()
        .setEndCursor(endCursor)
        .setHasNextPage(hasNextPage)
        .build());

    return responseBuilder.build();
  }

  public GetInstitutionNameResponse getInstitutionName(GetInstitutionNameRequest request) {

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

    return institutionReviewRepository
        .save(InstitutionReview.fromProto(request, institution, rating)).toProto(userName);
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

    // 소프트 삭제 처리
    institutionReview.setDeletedAt(new Date());
    institutionReviewRepository.save(institutionReview);

    return DeleteInstitutionReviewResponse.newBuilder().setSuccess(true).build();
  }

  @Transactional
  public void likeInstitutionReview(LikeInstitutionReviewRequestDto requestDto) {
    // 1. 리뷰가 존재하는지 확인
    InstitutionReview institutionReview = institutionReviewRepository.findById(
            UUID.fromString(requestDto.getInstitutionReviewId()))
        .orElseThrow(() -> Status.NOT_FOUND
            .withDescription("Institution review not found, wrong institution review id")
            .asRuntimeException()
        );

    // 2. 이미 좋아요를 눌렀는지 확인
    boolean existsByUserIdAndInstitutionReviewId = institutionReviewLikeRepository
        .existsByIdInstitutionReviewIdAndIdUserId(
            UUID.fromString(requestDto.getInstitutionReviewId()),
            UUID.fromString(requestDto.getUserId()));

    if (existsByUserIdAndInstitutionReviewId) {
      throw Status.INVALID_ARGUMENT
          .withDescription("User already liked this review")
          .asRuntimeException();
    }

    // 3. 좋아요 생성
    InstitutionReviewLike newLike = new InstitutionReviewLike(
        new InstitutionReviewLikeId(institutionReview.getId(),
            UUID.fromString(requestDto.getUserId())), LocalDateTime.now());

    institutionReviewLikeRepository.save(newLike);

    institutionReview.setLikeCount(institutionReview.getLikeCount() + 1);
  }

  @Transactional
  public void unlikeInstitutionReview(UnlikeInstitutionReviewRequestDto requestDto) {

    // 1. 리뷰가 존재하는지 확인
    InstitutionReview institutionReview = institutionReviewRepository.findById(
            UUID.fromString(requestDto.getInstitutionReviewId()))
        .orElseThrow(() -> Status.NOT_FOUND
            .withDescription("Institution review not found, wrong institution review id")
            .asRuntimeException()
        );

    // 2. 좋아요가 존재하는지 확인
    InstitutionReviewLike like = institutionReviewLikeRepository
        .findById(new InstitutionReviewLikeId(institutionReview.getId(),
            UUID.fromString(requestDto.getUserId())))
        .orElseThrow(() -> Status.NOT_FOUND
            .withDescription(
                "Institution review like not found, wrong institution review id or you didn't like this review")
            .asRuntimeException()
        );

    // 3. 좋아요 삭제
    institutionReviewLikeRepository.delete(like);

    institutionReview.setLikeCount(institutionReview.getLikeCount() - 1);
  }

  public InstitutionReviewResponse institutionReview(InstitutionReviewRequest request) {

    InstitutionReview institutionReview = institutionReviewRepository.findByIdWithInstitution(
            UUID.fromString(request.getInstitutionReviewId()))
        .orElseThrow(() -> Status.NOT_FOUND
            .withDescription("Institution review not found, wrong institution review id")
            .asRuntimeException()
        );

    String username = userServiceBlockingStub.getUserNameById(
        GetUserNameByIdRequest.newBuilder().setUserId((institutionReview.getUserId().toString()))
            .build()
    ).getUserName();

    return institutionReview.toProto(username);
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

  public InstitutionReviewsResponse institutionReviews(InstitutionReviewsRequest request) {

    List<InstitutionReviewProjection> reviews = institutionReviewRepository.findReviewsWithCursor(
        "-1".equals(request.getUserId()) ? null : UUID.fromString(request.getUserId()),
        request.getInstitutionCategoryId(),
        request.getCursor().isEmpty() ? null : UUID.fromString(request.getCursor()),
        request.getPageSize()
    );

    List<String> userIds = reviews.stream()
        .map(InstitutionReviewProjection::getUserId)
        .map(UUID::toString)
        .collect(Collectors.toList());

    Map<String, String> userNameMap = userServiceBlockingStub.getUserNameByIds(
        GetUserNameByIdsRequest.newBuilder().addAllUserIds(userIds).build()
    ).getUsersMap();

    List<InstitutionReviewForList> listInstitutionReview = reviews.stream()
        .map(review -> {
          String userId = review.getUserId().toString();
          String userName = userNameMap.get(userId);

          InstitutionReviewUser user = InstitutionReviewUser.newBuilder()
              .setId(userId)
              .setName(userName)
              .build();

          return InstitutionReviewForList.newBuilder()
              .setId(review.getId().toString())
              .setInstitution(InstitutionReviewInstitution.newBuilder()
                  .setInstitutionId(review.getInstitutionId().toString())
                  .setInstitutionName(review.getInstitutionName())
                  .setInstitutionCategoryId(review.getInstitutionCategoryId())
                  .build())
              .setUser(user)
              .setRating(review.getRating())
              .setMainTasks(review.getMainTasks())
              .setProsCons(review.getProsCons())
              .setAverageWorkhours(review.getAverageWorkhours())
              .setLikeCount(review.getLikeCount())
              .setCreatedAt(review.getCreatedAt().toString())
              .setIsLiked(review.getIsLiked())
              .build();
        })
        .collect(Collectors.toList());

    return InstitutionReviewsResponse.newBuilder()
        .addAllListInstitutionReview(listInstitutionReview)
        .setPageInfo(PageInfo.newBuilder()
            .setEndCursor(
                reviews.isEmpty() ? "" : reviews.get(reviews.size() - 1).getId().toString())
            .setHasNextPage(reviews.size() == request.getPageSize())
            .build())
        .build();

  }

  public MyInstitutionReviewsResponse myInstitutionReviews(MyInstitutionReviewsRequest request) {
    String username = userServiceBlockingStub.getUserNameById(
        GetUserNameByIdRequest.newBuilder().setUserId(request.getUserId()).build()
    ).getUserName();

    List<InstitutionReviewProjection> reviews = institutionReviewRepository
        .findMyInstitutionReviews(UUID.fromString(request.getUserId()));

    List<InstitutionReviewForList> listInstitutionReview = reviews.stream()
        .map(review -> {
          InstitutionReviewUser user = InstitutionReviewUser.newBuilder()
              .setId(request.getUserId())
              .setName(username)
              .build();

          return InstitutionReviewForList.newBuilder()
              .setId(review.getId().toString())
              .setInstitution(InstitutionReviewInstitution.newBuilder()
                  .setInstitutionId(review.getInstitutionId().toString())
                  .setInstitutionName(review.getInstitutionName())
                  .setInstitutionCategoryId(review.getInstitutionCategoryId())
                  .build())
              .setUser(user)
              .setRating(review.getRating())
              .setMainTasks(review.getMainTasks())
              .setProsCons(review.getProsCons())
              .setAverageWorkhours(review.getAverageWorkhours())
              .setLikeCount(review.getLikeCount())
              .setCreatedAt(review.getCreatedAt().toString())
              .setIsLiked(review.getIsLiked())
              .build();
        }).toList();

    return MyInstitutionReviewsResponse.newBuilder()
        .addAllListMyInstitutionReview(listInstitutionReview)
        .build();
  }

  public InstitutionReviewsByInstitutionResponse institutionReviewsByInstitution(
      InstitutionReviewsByInstitutionRequest request) {
    List<InstitutionReviewProjection> reviews = institutionReviewRepository.findInstitutionReviewsByInstitutionIdWithCursor(
        request.getUserId().isEmpty() ? null : UUID.fromString(request.getUserId()),
        UUID.fromString(request.getInstitutionId()),
        request.getCursor().isEmpty() ? null : UUID.fromString(request.getCursor()),
        request.getPageSize()
    );

    List<String> userIds = reviews.stream()
        .map(InstitutionReviewProjection::getUserId)
        .map(UUID::toString)
        .collect(Collectors.toList());

    Map<String, String> userNameMap = userServiceBlockingStub.getUserNameByIds(
        GetUserNameByIdsRequest.newBuilder().addAllUserIds(userIds).build()
    ).getUsersMap();

    List<InstitutionReviewForList> listInstitutionReview = reviews.stream()
        .map(review -> {
          String userId = review.getUserId().toString();
          String userName = userNameMap.get(userId);

          InstitutionReviewUser user = InstitutionReviewUser.newBuilder()
              .setId(userId)
              .setName(userName)
              .build();

          return InstitutionReviewForList.newBuilder()
              .setId(review.getId().toString())
              .setInstitution(InstitutionReviewInstitution.newBuilder()
                  .setInstitutionId(review.getInstitutionId().toString())
                  .setInstitutionName(review.getInstitutionName())
                  .setInstitutionCategoryId(review.getInstitutionCategoryId())
                  .build())
              .setUser(user)
              .setRating(review.getRating())
              .setMainTasks(review.getMainTasks())
              .setProsCons(review.getProsCons())
              .setAverageWorkhours(review.getAverageWorkhours())
              .setLikeCount(review.getLikeCount())
              .setCreatedAt(review.getCreatedAt().toString())
              .setIsLiked(review.getIsLiked())
              .build();
        })
        .collect(Collectors.toList());

    return InstitutionReviewsByInstitutionResponse.newBuilder()
        .addAllListInstitutionReviewByInstitution(listInstitutionReview)
        .setPageInfo(PageInfo.newBuilder()
            .setEndCursor(
                reviews.isEmpty() ? "" : reviews.get(reviews.size() - 1).getId().toString())
            .setHasNextPage(reviews.size() == request.getPageSize())
            .build())
        .build();

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