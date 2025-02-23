package org.example.gongiklifeclientbeinstitutionservice.service;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewForList;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewInstitution;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewUser;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.MyInstitutionReviewsRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.MyInstitutionReviewsResponse;
import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdRequest;
import io.grpc.Status;
import java.util.List;
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
public class MyInstitutionReviewsService {

  private final InstitutionReviewRepository institutionReviewRepository;

  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

  public MyInstitutionReviewsResponse myInstitutionReviews(MyInstitutionReviewsRequest request) {
    log.info("Fetching institution reviews for user: {}", request.getUserId());

    String username = fetchUsername(request.getUserId());
    List<InstitutionReviewProjection> reviews = fetchUserReviews(request.getUserId());
    List<InstitutionReviewForList> reviewList = convertToReviewList(reviews, request.getUserId(),
        username);

    return buildResponse(reviewList);
  }

  private String fetchUsername(String userId) {
    try {
      return userServiceBlockingStub.getUserNameById(
          GetUserNameByIdRequest.newBuilder()
              .setUserId(userId)
              .build()
      ).getUserName();
    } catch (Exception e) {
      log.error("Error fetching username for userId: {}", userId, e);
      throw createServiceException("Failed to fetch user information", e);
    }
  }

  private List<InstitutionReviewProjection> fetchUserReviews(String userId) {
    try {
      return institutionReviewRepository.findMyInstitutionReviews(parseUUID(userId));
    } catch (Exception e) {
      log.error("Error fetching reviews for userId: {}", userId, e);
      throw createServiceException("Failed to fetch user reviews", e);
    }
  }

  private List<InstitutionReviewForList> convertToReviewList(
      List<InstitutionReviewProjection> reviews,
      String userId,
      String username) {
    return reviews.stream()
        .map(review -> buildInstitutionReviewForList(review, userId, username))
        .collect(Collectors.toList());
  }

  private InstitutionReviewForList buildInstitutionReviewForList(
      InstitutionReviewProjection review,
      String userId,
      String username) {
    return InstitutionReviewForList.newBuilder()
        .setId(review.getId().toString())
        .setInstitution(buildInstitutionInfo(review))
        .setUser(buildUserInfo(userId, username))
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

  private InstitutionReviewUser buildUserInfo(String userId, String username) {
    return InstitutionReviewUser.newBuilder()
        .setId(userId)
        .setName(username)
        .build();
  }

  private MyInstitutionReviewsResponse buildResponse(List<InstitutionReviewForList> reviewList) {
    return MyInstitutionReviewsResponse.newBuilder()
        .addAllListMyInstitutionReview(reviewList)
        .build();
  }

  private UUID parseUUID(String id) {
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw createInvalidArgumentException("Invalid UUID format");
    }
  }

  private RuntimeException createServiceException(String message, Throwable cause) {
    return Status.INTERNAL.withDescription(message).withCause(cause).asRuntimeException();
  }

  private RuntimeException createInvalidArgumentException(String message) {
    return Status.INVALID_ARGUMENT.withDescription(message).asRuntimeException();
  }
}
