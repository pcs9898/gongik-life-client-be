package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.community.searchPosts.SearchPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.searchPosts.SearchPostsResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchPostsService {

    @GrpcClient("gongik-life-client-be-community-service")
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    public SearchPostsResponseDto searchPosts(SearchPostsRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("SearchPostsService",
                () -> SearchPostsResponseDto.fromSearchPostsResponseProto(
                        communityServiceBlockingStub.searchPosts(requestDto.toSearchPostsRequestProto())
                ));
    }
}
