package org.example.gongiklifeclientbegraphql.service.institution;

import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviewsByInstitution.InstitutionReviewsByInstitutionRequestDto;
import org.example.gongiklifeclientbegraphql.dto.institution.institutionReviewsByInstitution.InstitutionReviewsByInstitutionResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InstitutionReviewsByInstitutionService {

    @GrpcClient("gongik-life-client-be-institution-service")
    private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;

    public InstitutionReviewsByInstitutionResponseDto institutionReviewsByInstitution(
            InstitutionReviewsByInstitutionRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("InstitutionReviewsByInstitutionService", () -> {
            return InstitutionReviewsByInstitutionResponseDto.fromProto(
                    institutionBlockingStub.institutionReviewsByInstitution(requestDto.toProto()));
        });
    }
}
