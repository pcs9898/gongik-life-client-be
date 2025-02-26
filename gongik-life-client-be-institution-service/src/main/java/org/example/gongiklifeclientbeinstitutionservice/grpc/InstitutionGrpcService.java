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
import io.grpc.stub.StreamObserver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.gongiklifeclientbeinstitutionservice.dto.InstitutionForWorkHoursStatisticsProjection;
import org.example.gongiklifeclientbeinstitutionservice.service.CreateInstitutionReviewService;
import org.example.gongiklifeclientbeinstitutionservice.service.DeleteInstitutionReviewService;
import org.example.gongiklifeclientbeinstitutionservice.service.GetInstitutionReviewService;
import org.example.gongiklifeclientbeinstitutionservice.service.GetInstitutionReviewsService;
import org.example.gongiklifeclientbeinstitutionservice.service.GetInstitutionService;
import org.example.gongiklifeclientbeinstitutionservice.service.InstitutionReviewsByInstitutionService;
import org.example.gongiklifeclientbeinstitutionservice.service.InstitutionService;
import org.example.gongiklifeclientbeinstitutionservice.service.LikeInstitutionReviewService;
import org.example.gongiklifeclientbeinstitutionservice.service.MyInstitutionReviewsService;
import org.example.gongiklifeclientbeinstitutionservice.service.SearchInstitutionService;
import util.GrpcServiceExceptionHandlingUtil;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class InstitutionGrpcService extends InstitutionServiceGrpc.InstitutionServiceImplBase {

  private final InstitutionService institutionService;
  private final SearchInstitutionService searchInstitutionService;
  private final GetInstitutionService getInstitutionService;
  private final CreateInstitutionReviewService createInstitutionReviewService;
  private final DeleteInstitutionReviewService deleteInstitutionReviewService;
  private final LikeInstitutionReviewService likeInstitutionReviewService;
  private final GetInstitutionReviewService getInstitutionReviewService;
  private final GetInstitutionReviewsService getInstitutionReviewsService;
  private final MyInstitutionReviewsService myInstitutionReviewsService;
  private final InstitutionReviewsByInstitutionService institutionReviewsByInstitutionService;

  @Override
  public void searchInstitutions(SearchInstitutionsRequest request,
      StreamObserver<SearchInstitutionsResponse> responseObserver) {
    GrpcServiceExceptionHandlingUtil.handle("searchInstitutions",
        () -> searchInstitutionService.searchInstitutions(request),
        responseObserver);
  }

  @Override
  public void getInstitutionName(GetInstitutionNameRequest request,
      StreamObserver<GetInstitutionNameResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("getInstitutionName",
        () -> institutionService.getInstitutionName(request),
        responseObserver);
  }

  @Override
  public void institution(InstitutionRequest request,
      StreamObserver<InstitutionResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("institution",
        () -> getInstitutionService.institution(request),
        responseObserver);
  }

  @Override
  public void createInstitutionReview(CreateInstitutionReviewRequest request,
      StreamObserver<InstitutionReviewResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("createInstitutionReview",
        () -> createInstitutionReviewService.createInstitutionReview(request),
        responseObserver);
  }

  @Override
  public void deleteInstitutionReview(DeleteInstitutionReviewRequest request,
      StreamObserver<DeleteInstitutionReviewResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("deleteInstitutionReview",
        () -> deleteInstitutionReviewService.deleteInstitutionReview(request),
        responseObserver);
  }

  @Override
  public void institutionReview(InstitutionReviewRequest request,
      StreamObserver<InstitutionReviewResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("institutionReview",
        () -> getInstitutionReviewService.institutionReview(request),
        responseObserver);
  }

  @Override
  public void isLikedInstitutionReview(IsLikedInstitutionReviewRequest request,
      StreamObserver<IsLikedInstitutionReviewResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("isLikedInstitutionReview",
        () -> institutionService.isLikedInstitutionReview(request),
        responseObserver);
  }

  @Override
  public void institutionReviews(InstitutionReviewsRequest request,
      StreamObserver<InstitutionReviewsResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("institutionReviews",
        () -> getInstitutionReviewsService.institutionReviews(request),
        responseObserver);
  }


  @Override
  public void myInstitutionReviews(MyInstitutionReviewsRequest request,
      StreamObserver<MyInstitutionReviewsResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("myInstitutionReviews",
        () -> myInstitutionReviewsService.myInstitutionReviews(request),
        responseObserver);
  }

  @Override
  public void institutionReviewsByInstitution(InstitutionReviewsByInstitutionRequest request,
      StreamObserver<InstitutionReviewsByInstitutionResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("institutionReviewsByInstitution",
        () -> institutionReviewsByInstitutionService.institutionReviewsByInstitution(request),
        responseObserver);
  }

  @Override
  public void getInstitutionReviewCount(GetInstitutionReviewCountRequest request,
      StreamObserver<GetInstitutionReviewCountResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("getInstitutionReviewCount",
        () -> institutionService.getInstitutionReviewCount(request),
        responseObserver);
  }

  @Override
  public void existsInstitution(ExistsInstitutionRequest request,
      StreamObserver<ExistsInstitutionResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("existsInstitution",
        () -> institutionService.existsInstitution(request),
        responseObserver);
  }

  @Override
  public void existsInstitutionReview(ExistsInstitutionReviewRequest request,
      StreamObserver<ExistsInstitutionReviewResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("existsInstitutionReview",
        () -> institutionService.existsInstitutionReview(request),
        responseObserver);
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

    GrpcServiceExceptionHandlingUtil.handle("getMyAverageWorkhours",
        () -> institutionService.getMyAverageWorkhours(request),
        responseObserver);
  }
}