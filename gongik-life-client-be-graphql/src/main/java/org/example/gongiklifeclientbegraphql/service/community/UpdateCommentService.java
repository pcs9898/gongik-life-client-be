package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.community.updateComment.UpdateCommentRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.updateComment.UpdateCommentResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateCommentService {

    @GrpcClient("gongik-life-client-be-community-service")
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    public UpdateCommentResponseDto updateComment(UpdateCommentRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("UpdateCommentService",
                () -> UpdateCommentResponseDto.fromUpdateCommentResponseProto(
                        communityServiceBlockingStub.updateComment(requestDto.toUpdateCommentRequestProto())
                ));
    }
}
