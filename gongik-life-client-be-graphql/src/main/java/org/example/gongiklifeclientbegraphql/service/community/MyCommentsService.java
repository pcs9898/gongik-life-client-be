package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.community.myComments.MyCommentsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.myComments.MyCommentsResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyCommentsService {

    @GrpcClient("gongik-life-client-be-community-service")
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    public MyCommentsResponseDto myComments(MyCommentsRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("MyCommentsService",
                () -> MyCommentsResponseDto.fromMyCommentsResponseProto(
                        communityServiceBlockingStub.myComments(requestDto.toMyCommentsRequestProto())
                ));
    }
}
