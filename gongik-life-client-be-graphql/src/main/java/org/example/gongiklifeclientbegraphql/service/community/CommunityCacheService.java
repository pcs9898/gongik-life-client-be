package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.GetPostRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.common.PostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.deletePost.DeletePostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.deletePost.DeletePostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.updatepost.UpdatePostRequestDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommunityCacheService {

    @GrpcClient("gongik-life-client-be-community-service")
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    @CachePut(value = "post", key = "#requestDto.postId")
    public PostResponseDto updatePost(UpdatePostRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("UpdatePostService", () -> {
            return PostResponseDto.fromUpdatePostResponseProto(communityServiceBlockingStub.updatePost(
                    requestDto.toUpdatePostRequestProto()));
        });
    }

    @CacheEvict(value = "post", key = "#requestDto.postId")
    public DeletePostResponseDto deletePost(DeletePostRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("DeletePostService", () -> {
            return DeletePostResponseDto.fromDeletePostResponseProto(communityServiceBlockingStub.deletePost(
                    requestDto.toDeletePostRequestProto()), requestDto.getPostId());
        });
    }

    @Cacheable(value = "post", key = "#postId")
    public PostResponseDto getPost(String postId) {

        return ServiceExceptionHandlingUtil.handle("GetPostService", () -> {
            return PostResponseDto.fromPostResponseProto(communityServiceBlockingStub.getPost(
                    GetPostRequest.newBuilder().setPostId(postId).build()));
        });
    }
}
