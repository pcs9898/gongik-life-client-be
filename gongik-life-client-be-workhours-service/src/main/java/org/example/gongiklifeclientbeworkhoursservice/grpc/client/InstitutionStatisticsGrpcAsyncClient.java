package org.example.gongiklifeclientbeworkhoursservice.grpc.client;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.Empty;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionForWorkHourStatistics;
import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbeworkhoursservice.dto.InstitutionDto;
import org.springframework.stereotype.Component;


@Component
public class InstitutionStatisticsGrpcAsyncClient {


  // @GrpcClient 어노테이션을 통해 gRPC 서버 주소 설정 파일명("gongik-life-client-be-institution-service")을 지정합니다.
  @GrpcClient("gongik-life-client-be-institution-service")
  private InstitutionServiceGrpc.InstitutionServiceStub asyncStub;

  /**
   * gRPC 서버 스트리밍 호출을 통해 기관 통계에 필요한 데이터를 청크 단위로 받아 List로 반환합니다.
   *
   * @return List&lt;InstitutionDTO&gt; 기관 데이터 리스트
   * @throws InterruptedException
   */
  public List<InstitutionDto> getInstitutionsForWorkhourStatistics() throws InterruptedException {
    List<InstitutionDto> institutions = new ArrayList<>();
    CountDownLatch latch = new CountDownLatch(1);

    StreamObserver<InstitutionForWorkHourStatistics> responseObserver = new StreamObserver<InstitutionForWorkHourStatistics>() {
      @Override
      public void onNext(InstitutionForWorkHourStatistics response) {
        // 서버에서 받은 데이터를 DTO로 변환하여 리스트에 추가
        institutions.add(new InstitutionDto(
            UUID.fromString(response.getId()),
            response.getInstitutionCategoryId(),
            response.getAverageWorkhours(),
            response.getReviewCount()
        ));
      }

      @Override
      public void onError(Throwable t) {
        System.err.println("Error during gRPC streaming: " + t.getMessage());
        latch.countDown();
      }

      @Override
      public void onCompleted() {
        System.out.println("Streaming completed. Total items received: " + institutions.size());
        latch.countDown();
      }
    };

    // gRPC 스트리밍 호출
    asyncStub.getInstitutionsForWorkHourStatistics(
        Empty.newBuilder().build(), responseObserver);

    // 스트림이 완료될 때까지 대기 (옵션: 타임아웃 지정 가능)
    latch.await();

    return institutions;
  }
}