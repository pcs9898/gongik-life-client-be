package org.example.gongiklifeclientbereportservice.service;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.institutionService.domain.service.InstitutionServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbereportservice.repository.ReportRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    @GrpcClient("gongik-life-client-be-community-service")
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;
    @GrpcClient("gongik-life-client-be-institution-service")
    private InstitutionServiceGrpc.InstitutionServiceBlockingStub institutionBlockingStub;


}


