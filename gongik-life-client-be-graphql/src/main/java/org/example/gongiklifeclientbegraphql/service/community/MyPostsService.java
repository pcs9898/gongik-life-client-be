package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.community.myPosts.MyPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.myPosts.MyPostsResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyPostsService {

    @GrpcClient("gongik-life-client-be-community-service")
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    public MyPostsResponseDto myPosts(MyPostsRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("MyPostsService",
                () -> MyPostsResponseDto.fromMyPostsResponseProto(
                        communityServiceBlockingStub.myPosts(requestDto.toMyPostsRequestProto())
                ));
    }
}
