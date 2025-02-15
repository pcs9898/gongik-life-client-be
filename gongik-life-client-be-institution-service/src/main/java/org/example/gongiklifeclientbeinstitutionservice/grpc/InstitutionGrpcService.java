package org.example.gongiklifeclientbeinstitutionservice.grpc;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.CreateInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionReviewCountRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionReviewCountResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsByInstitutionRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsByInstitutionResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionReviewsResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.IsLikedInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.IsLikedInstitutionReviewResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.MyInstitutionReviewsRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.MyInstitutionReviewsResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.gongiklifeclientbeinstitutionservice.service.InstitutionService;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class InstitutionGrpcService extends InstitutionServiceGrpc.InstitutionServiceImplBase {

  private final InstitutionService institutionService;

  @Override
  public void searchInstitutions(SearchInstitutionsRequest request,
      StreamObserver<SearchInstitutionsResponse> responseObserver) {

    try {
      SearchInstitutionsResponse response = institutionService.searchInstitutions(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("searchInstitutions error : ", e);
      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }

  @Override
  public void getInstitutionName(GetInstitutionNameRequest request,
      StreamObserver<GetInstitutionNameResponse> responseObserver) {
    try {
      GetInstitutionNameResponse response = institutionService.getInstitutionName(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("getInstitutionName error : ", e);

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }

  @Override
  public void institution(InstitutionRequest request,
      StreamObserver<InstitutionResponse> responseObserver) {
    try {
      InstitutionResponse response = institutionService.institution(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("institution error : ", e);

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }

  @Override
  public void createInstitutionReview(CreateInstitutionReviewRequest request,
      StreamObserver<InstitutionReviewResponse> responseObserver) {
    try {
      InstitutionReviewResponse response = institutionService.createInstitutionReview(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("createInstitutionReview error : ", e);

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );

    }
  }

  @Override
  public void deleteInstitutionReview(DeleteInstitutionReviewRequest request,
      StreamObserver<DeleteInstitutionReviewResponse> responseObserver) {
    try {
      DeleteInstitutionReviewResponse response = institutionService.deleteInstitutionReview(
          request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("deleteInstitutionReview error : ", e);

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }

  @Override
  public void institutionReview(InstitutionReviewRequest request,
      StreamObserver<InstitutionReviewResponse> responseObserver) {
    try {
      InstitutionReviewResponse response = institutionService.institutionReview(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("institutionReview error : ", e);

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }

  @Override
  public void isLikedInstitutionReview(IsLikedInstitutionReviewRequest request,
      StreamObserver<IsLikedInstitutionReviewResponse> responseObserver) {
    try {
      IsLikedInstitutionReviewResponse response = institutionService.isLikedInstitutionReview(
          request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("isLikedInstitutionReview error : ", e);

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }

  @Override
  public void institutionReviews(InstitutionReviewsRequest request,
      StreamObserver<InstitutionReviewsResponse> responseObserver) {
    try {
      InstitutionReviewsResponse response = institutionService.institutionReviews(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("institutionReviews error : ", e);

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }


  @Override
  public void myInstitutionReviews(MyInstitutionReviewsRequest request,
      StreamObserver<MyInstitutionReviewsResponse> responseObserver) {
    try {
      MyInstitutionReviewsResponse response = institutionService.myInstitutionReviews(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("myInstitutionReviews error : ", e);

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }

  @Override
  public void institutionReviewsByInstitution(InstitutionReviewsByInstitutionRequest request,
      StreamObserver<InstitutionReviewsByInstitutionResponse> responseObserver) {
    try {
      InstitutionReviewsByInstitutionResponse response = institutionService
          .institutionReviewsByInstitution(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("institutionReviewsByInstitution error : ", e);

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }

  @Override
  public void getInstitutionReviewCount(GetInstitutionReviewCountRequest request,
      StreamObserver<GetInstitutionReviewCountResponse> responseObserver) {
    try {
      GetInstitutionReviewCountResponse response = institutionService.getInstitutionReviewCount(
          request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("getInstitutionReviewCount error : ", e);

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }
}