package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostAndCommentCountResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.community.post.PostRequestDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommunityService {

    @GrpcClient("gongik-life-client-be-community-service")
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    public Boolean isLikedPost(String postId, String userId) {

        return ServiceExceptionHandlingUtil.handle("isLikedPostService",
                () -> {

                    IsLikedPostRequest request = IsLikedPostRequest.newBuilder()
                            .setPostId(postId)
                            .setUserId(userId)
                            .build();
                    return communityServiceBlockingStub.isLikedPost(request)
                            .getIsLiked();
                });
    }

    public IsLikedPostAndCommentCountResponse isLikedPostAndCommentCount(PostRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("isLikedPostAndCommentCountService",
                () -> communityServiceBlockingStub.isLikedPostAndCommentCount(requestDto.toIsLikedPostAndCommentCountRequestProto()));
    }


}