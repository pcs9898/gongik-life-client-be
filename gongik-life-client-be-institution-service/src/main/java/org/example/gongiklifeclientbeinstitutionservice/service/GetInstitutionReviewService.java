package org.example.gongiklifeclientbeinstitutionservice.service;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewResponse;
import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdRequest;
import io.grpc.Status;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionReview;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionReviewRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetInstitutionReviewService {

  private final InstitutionReviewRepository institutionReviewRepository;

  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

  public InstitutionReviewResponse institutionReview(InstitutionReviewRequest request) {
    log.info("Fetching institution review for id: {}", request.getInstitutionReviewId());

    InstitutionReview review = findReviewWithInstitution(request.getInstitutionReviewId());
    String username = fetchUserName(review.getUserId());

    log.info("Successfully retrieved review for user: {}", username);
    return review.toProto(username);
  }

  private InstitutionReview findReviewWithInstitution(String reviewId) {
    return institutionReviewRepository.findByIdWithInstitution(parseUUID(reviewId))
        .orElseThrow(() -> createNotFoundException(
            "Institution review not found, wrong institution review id"));
  }

  private String fetchUserName(UUID userId) {
    try {
      return userServiceBlockingStub.getUserNameById(
          GetUserNameByIdRequest.newBuilder()
              .setUserId(userId.toString())
              .build()
      ).getUserName();
    } catch (Exception e) {
      log.error("Error fetching username for userId: {}", userId, e);
      throw createServiceException("Failed to fetch user information", e);
    }
  }


  private UUID parseUUID(String id) {
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw createInvalidArgumentException("Invalid UUID format");
    }
  }

  private RuntimeException createNotFoundException(String message) {
    return Status.NOT_FOUND.withDescription(message).asRuntimeException();
  }

  private RuntimeException createServiceException(String message, Throwable cause) {
    return Status.INTERNAL.withDescription(message).withCause(cause).asRuntimeException();
  }

  private RuntimeException createInvalidArgumentException(String message) {
    return Status.INVALID_ARGUMENT.withDescription(message).asRuntimeException();
  }
}
