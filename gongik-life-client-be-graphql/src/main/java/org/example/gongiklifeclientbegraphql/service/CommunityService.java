package org.example.gongiklifeclientbegraphql.service;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostRequest;
import dto.community.LikePostRequestDto;
import dto.community.UnLikePostRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.common.PostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.createPost.CreatePostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.likePost.LikePostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.unLikePost.UnLikePostResponseDto;
import org.example.gongiklifeclientbegraphql.producer.community.LikePostProducer;
import org.example.gongiklifeclientbegraphql.producer.community.UnLikePostProducer;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommunityService {

  private final LikePostProducer likePostProducer;
  private final UnLikePostProducer unLikePostProducer;

  @GrpcClient("gongik-life-client-be-community-service")
  private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

  public PostResponseDto createPost(CreatePostRequestDto requestDto) {
    try {
      return PostResponseDto.fromCreatePostResponseProto(communityServiceBlockingStub.createPost(
          requestDto.toProto()));
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }


  public Boolean isLikedPost(String postId, String userId) {
    try {
      return communityServiceBlockingStub.isLikedPost(
          IsLikedPostRequest.newBuilder()
              .setPostId(postId)
              .setUserId(userId)
              .build()
      ).getIsLiked();
    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  public LikePostResponseDto likePost(LikePostRequestDto requestDto) {
    try {

      likePostProducer.sendLikePostRequest(requestDto);

      return LikePostResponseDto.builder().success(true).build();

    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }

  public UnLikePostResponseDto unLikePost(UnLikePostRequestDto requestDto) {
    try {

      unLikePostProducer.sendUnLikePostRequest(requestDto);

      return UnLikePostResponseDto.builder().success(true).build();

    } catch (Exception e) {
      log.error("gRPC 호출 중 오류 발생: ", e);
      throw e;
    }
  }
}
