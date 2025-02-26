package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.community.deleteComment.DeleteCommentRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.deleteComment.DeleteCommentResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteCommentService {

    @GrpcClient("gongik-life-client-be-community-service")
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    public DeleteCommentResponseDto deleteComment(DeleteCommentRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("DeleteCommentService",
                () -> DeleteCommentResponseDto.fromDeleteCommentResponseProto(
                        communityServiceBlockingStub.deleteComment(requestDto.toDeleteCommentRequestProto())
                ));
    }
}
