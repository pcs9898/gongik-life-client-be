package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.community.userPosts.UserPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.userPosts.UserPostsResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserPostsService {

    @GrpcClient("gongik-life-client-be-community-service")
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    public UserPostsResponseDto userPosts(UserPostsRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("UserPostsService",
                () -> UserPostsResponseDto.fromUserPostsResponseProto(
                        communityServiceBlockingStub.userPosts(requestDto.toUserPostsRequestProto())
                ));
    }
}
