package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.community.myLikedPosts.MyLikedPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.myLikedPosts.MyLikedPostsResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyLikedPostsService {

    @GrpcClient("gongik-life-client-be-community-service")
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    public MyLikedPostsResponseDto myLikedPosts(MyLikedPostsRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("MyLikedPostsService",
                () -> MyLikedPostsResponseDto.fromMyLikedPostsResponseProto(
                        communityServiceBlockingStub.myLikedPosts(requestDto.toMyLikedPostsRequestProto())
                ));
    }
}
