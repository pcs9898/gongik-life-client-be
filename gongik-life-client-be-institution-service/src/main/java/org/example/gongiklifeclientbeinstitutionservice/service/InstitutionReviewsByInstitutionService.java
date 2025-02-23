package org.example.gongiklifeclientbeinstitutionservice.service;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewForList;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewInstitution;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewUser;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsByInstitutionRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsByInstitutionResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.PageInfo;
import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdsRequest;
import io.grpc.Status;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbeinstitutionservice.dto.InstitutionReviewProjection;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstitutionReviewsByInstitutionService {

  private final InstitutionReviewRepository institutionReviewRepository;

  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

  public InstitutionReviewsByInstitutionResponse institutionReviewsByInstitution(
      InstitutionReviewsByInstitutionRequest request) {
    log.info("Fetching institution reviews for institution: {}", request.getInstitutionId());

    List<InstitutionReviewProjection> reviews = fetchReviews(request);
    Map<String, String> userNameMap = fetchUserNames(reviews);
    List<InstitutionReviewForList> reviewList = convertToReviewList(reviews, userNameMap);

    return buildResponse(reviewList, reviews, request.getPageSize());
  }

  private List<InstitutionReviewProjection> fetchReviews(
      InstitutionReviewsByInstitutionRequest request) {
    UUID userId = request.getUserId().isEmpty() ? null : parseUUID(request.getUserId());
    UUID institutionId = parseUUID(request.getInstitutionId());
    UUID cursor = request.getCursor().isEmpty() ? null : parseUUID(request.getCursor());

    return institutionReviewRepository.findInstitutionReviewsByInstitutionIdWithCursor(
        userId,
        institutionId,
        cursor,
        request.getPageSize()
    );
  }

  private Map<String, String> fetchUserNames(List<InstitutionReviewProjection> reviews) {
    List<String> userIds = extractUserIds(reviews);
    try {
      return userServiceBlockingStub.getUserNameByIds(
          GetUserNameByIdsRequest.newBuilder()
              .addAllUserIds(userIds)
              .build()
      ).getUsersMap();
    } catch (Exception e) {
      log.error("Error fetching usernames for users: {}", userIds, e);
      throw createServiceException("Failed to fetch user information", e);
    }
  }

  private List<String> extractUserIds(List<InstitutionReviewProjection> reviews) {
    return reviews.stream()
        .map(InstitutionReviewProjection::getUserId)
        .map(UUID::toString)
        .collect(Collectors.toList());
  }

  private List<InstitutionReviewForList> convertToReviewList(
      List<InstitutionReviewProjection> reviews,
      Map<String, String> userNameMap) {
    return reviews.stream()
        .map(review -> buildInstitutionReviewForList(review, userNameMap))
        .collect(Collectors.toList());
  }

  private InstitutionReviewForList buildInstitutionReviewForList(
      InstitutionReviewProjection review,
      Map<String, String> userNameMap) {
    String userId = review.getUserId().toString();

    return InstitutionReviewForList.newBuilder()
        .setId(review.getId().toString())
        .setInstitution(buildInstitutionInfo(review))
        .setUser(buildUserInfo(userId, userNameMap.get(userId)))
        .setRating(review.getRating())
        .setMainTasks(review.getMainTasks())
        .setProsCons(review.getProsCons())
        .setAverageWorkhours(review.getAverageWorkhours())
        .setLikeCount(review.getLikeCount())
        .setCreatedAt(review.getCreatedAt().toString())
        .setIsLiked(review.getIsLiked())
        .build();
  }

  private InstitutionReviewInstitution buildInstitutionInfo(InstitutionReviewProjection review) {
    return InstitutionReviewInstitution.newBuilder()
        .setInstitutionId(review.getInstitutionId().toString())
        .setInstitutionName(review.getInstitutionName())
        .setInstitutionCategoryId(review.getInstitutionCategoryId())
        .build();
  }

  private InstitutionReviewUser buildUserInfo(String userId, String userName) {
    return InstitutionReviewUser.newBuilder()
        .setId(userId)
        .setName(userName)
        .build();
  }

  private InstitutionReviewsByInstitutionResponse buildResponse(
      List<InstitutionReviewForList> reviewList,
      List<InstitutionReviewProjection> originalReviews,
      int pageSize) {
    return InstitutionReviewsByInstitutionResponse.newBuilder()
        .addAllListInstitutionReviewByInstitution(reviewList)
        .setPageInfo(buildPageInfo(originalReviews, pageSize))
        .build();
  }

  private PageInfo buildPageInfo(List<InstitutionReviewProjection> reviews, int pageSize) {
    return PageInfo.newBuilder()
        .setEndCursor(reviews.isEmpty() ? "" : reviews.get(reviews.size() - 1).getId().toString())
        .setHasNextPage(reviews.size() == pageSize)
        .build();
  }

  private UUID parseUUID(String id) {
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw createInvalidArgumentException("Invalid UUID format: " + id);
    }
  }

  private RuntimeException createServiceException(String message, Throwable cause) {
    return Status.INTERNAL.withDescription(message).withCause(cause).asRuntimeException();
  }

  private RuntimeException createInvalidArgumentException(String message) {
    return Status.INVALID_ARGUMENT.withDescription(message).asRuntimeException();
  }
}
