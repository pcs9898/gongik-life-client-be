package org.example.gongiklifeclientbeinstitutionservice.grpc;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.CreateInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.Empty;
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
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionForWorkHourStatistics;
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
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.gongiklifeclientbeinstitutionservice.dto.InstitutionForWorkHoursStatisticsProjection;
import org.example.gongiklifeclientbeinstitutionservice.service.InstitutionService;
import util.GrpcServiceExceptionHandlingUtil;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class InstitutionGrpcService extends InstitutionServiceGrpc.InstitutionServiceImplBase {

  private final InstitutionService institutionService;

//  @Override
//  public void searchInstitutions(SearchInstitutionsRequest request,
//      StreamObserver<SearchInstitutionsResponse> responseObserver) {
//
//    try {
//      SearchInstitutionsResponse response = institutionService.searchInstitutions(request);
//
//      responseObserver.onNext(response);
//      responseObserver.onCompleted();
//    } catch (Exception e) {
//      log.info("searchInstitutions error : ", e);
//      responseObserver.onError(
//          Status.INTERNAL
//              .withDescription(e.getMessage())
//              .withCause(e)  // 원인 예외 포함
//              .asRuntimeException()
//      );
//    }
//  }

  @Override
  public void searchInstitutions(SearchInstitutionsRequest request,
      StreamObserver<SearchInstitutionsResponse> responseObserver) {
    GrpcServiceExceptionHandlingUtil.handle("searchInstitutions",
        () -> institutionService.searchInstitutions(request),
        responseObserver);
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

  @Override
  public void existsInstitution(ExistsInstitutionRequest request,
      StreamObserver<ExistsInstitutionResponse> responseObserver) {
    try {
      ExistsInstitutionResponse response = institutionService.existsInstitution(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("existsInstitution error : ", e);

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }

  @Override
  public void existsInstitutionReview(ExistsInstitutionReviewRequest request,
      StreamObserver<ExistsInstitutionReviewResponse> responseObserver) {
    try {
      ExistsInstitutionReviewResponse response = institutionService.existsInstitutionReview(
          request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("existsInstitutionReview error : ", e);

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }

  @Override
  public void getInstitutionsForWorkHourStatistics(Empty request,
      StreamObserver<InstitutionForWorkHourStatistics> responseObserver) {
    try {
      // DB에서 모든 기관 데이터 조회 (여기서는 예시로 List<InstitutionEntity>를 가정)
      List<InstitutionForWorkHoursStatisticsProjection> projections = institutionService.getInstitutionsForWorkHourStatistics();

      // 데이터를 청크 단위로 스트리밍 (예: 한 번에 500건씩)
      int chunkSize = 500;
      int i = 0;
      for (InstitutionForWorkHoursStatisticsProjection proj : projections) {
        InstitutionForWorkHourStatistics institutionMessage = InstitutionForWorkHourStatistics.newBuilder()
            .setId(proj.getId().toString())
            .setInstitutionCategoryId(proj.getInstitutionCategoryId())
            .setAverageWorkhours(proj.getAverageWorkhours())
            .setReviewCount(proj.getReviewCount())
            .build();

        responseObserver.onNext(institutionMessage);

        if ((++i) % chunkSize == 0) {
          Thread.sleep(10); // 전송 속도 조절 (옵션)
        }
      }

      // 스트림 완료
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onError(e); // 에러 발생 시 처리
    }
  }

  @Override
  public void getMyAverageWorkhours(GetMyAverageWorkhoursRequest request,
      StreamObserver<GetMyAverageWorkhoursResponse> responseObserver) {
    try {
      GetMyAverageWorkhoursResponse response = institutionService.getMyAverageWorkhours(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("getMyAverageWorkhours error : ", e);

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }
}