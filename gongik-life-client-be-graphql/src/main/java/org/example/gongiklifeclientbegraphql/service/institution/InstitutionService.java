package org.example.gongiklifeclientbegraphql.service.institution;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.IsLikedInstitutionReviewRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InstitutionService {


    private final LikeInstitutionReviewService InstitutionCacheService;

    @GrpcClient("gongik-life-client-be-institution-service")
    private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

    public Boolean isLikedInstitutionReview(String institutionReviewId, String userId) {

        return ServiceExceptionHandlingUtil.handle("isLikedInstitutionReviewInstitutionService", () -> {
            return institutionBlockingStub.isLikedInstitutionReview(
                    IsLikedInstitutionReviewRequest.newBuilder()
                            .setInstitutionReviewId(institutionReviewId)
                            .setUserId(userId)
                            .build()
            ).getIsLiked();
        });
    }


    public Integer getMyAverageWorkhours(String userId, String userInstitutionId) {

        return ServiceExceptionHandlingUtil.handle("getMyAverageWorkhoursInstitutionService", () -> {
            return institutionBlockingStub.getMyAverageWorkhours(
                    com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.GetMyAverageWorkhoursRequest.newBuilder()
                            .setUserId(userId)
                            .setInstitutionId(userInstitutionId)
                            .build()
            ).getMyAverageWorkhours();
        });
    }
}