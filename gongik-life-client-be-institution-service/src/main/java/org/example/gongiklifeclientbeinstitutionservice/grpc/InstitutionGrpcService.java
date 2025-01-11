package org.example.gongiklifeclientbeinstitutionservice.grpc;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetInstitutionNameResponse;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsRequest;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitutionsResponse;
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
      responseObserver.onError(e);
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
      responseObserver.onError(e);
    }
  }
}