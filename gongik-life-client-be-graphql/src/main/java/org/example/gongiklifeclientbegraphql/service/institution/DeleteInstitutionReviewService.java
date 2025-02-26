package org.example.gongiklifeclientbegraphql.service.institution;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.DeleteInstitutionReviewResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.institution.deleteInstitutionReview.DeleteInstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.deleteInstitutionReview.DeleteInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteInstitutionReviewService {

    @GrpcClient("gongik-life-client-be-institution-service")
    private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

    @CacheEvict(value = "institutionReview", key = "#requestDto.getInstitutionReviewId()")
    public DeleteInstitutionReviewResponseDto deleteInstitutionReview(
            DeleteInstitutionReviewRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("DeleteInstitutionReviewService", () -> {
            DeleteInstitutionReviewResponse result = institutionBlockingStub.deleteInstitutionReview(
                    requestDto.toProto());

            if (!result.getSuccess()) {
                throw new RuntimeException("Failed to delete institution review");
            }

            return DeleteInstitutionReviewResponseDto.fromProto(
                    requestDto.getInstitutionReviewId()
            );
        });
    }

}
