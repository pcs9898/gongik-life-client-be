package org.example.gongiklifeclientbegraphql.service.institution;


import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.institution.createInsitutionReview.CreateInstitutionReviewRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.createInsitutionReview.CreateInstitutionReviewResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateInstitutionReviewService {

    @GrpcClient("gongik-life-client-be-institution-service")
    private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

    public CreateInstitutionReviewResponseDto createInstitutionReview(
            CreateInstitutionReviewRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("CreateInstitutionReviewService", () -> {
            return CreateInstitutionReviewResponseDto.fromInstitutionReviewResponseProto(
                    institutionBlockingStub.createInstitutionReview(
                            requestDto.toCreateInstitutionReviewRequestProto()));
        });
    }
}
