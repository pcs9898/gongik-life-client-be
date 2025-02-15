package org.example.gongiklifeclientbegraphql.service;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.GetPostRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.common.PostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.deletePost.DeletePostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.deletePost.DeletePostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.updatepost.UpdatePostRequestDto;
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
    try {
      return PostResponseDto.fromUpdatePostResponseProto(communityServiceBlockingStub.updatePost(
          requestDto.toProto()));
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  @CacheEvict(value = "post", key = "#requestDto.postId")
  public DeletePostResponseDto deletePost(DeletePostRequestDto requestDto) {
    try {
      return DeletePostResponseDto.fromProto(communityServiceBlockingStub.deletePost(
          requestDto.toProto()), requestDto.getPostId());
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  @Cacheable(value = "post", key = "#postId")
  public PostResponseDto getPost(String postId) {
    try {
      return PostResponseDto.fromPostResponseProto(communityServiceBlockingStub.getPost(
          GetPostRequest.newBuilder().setPostId(postId).build()));
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }
}
