package org.example.gongiklifeclientbecommunityservice.grpc;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreatePostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreatePostResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.DeletePostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.DeletePostResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdatePostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdatePostResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.gongiklifeclientbecommunityservice.producer.DeleteAllCommentsByPostProducer;
import org.example.gongiklifeclientbecommunityservice.service.PostService;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class CommunityGrpcService extends CommunityServiceGrpc.CommunityServiceImplBase {

  private final DeleteAllCommentsByPostProducer deleteAllCommentsByPostProducer;
  private final PostService postService;

  @Override
  public void createPost(CreatePostRequest request,
      StreamObserver<CreatePostResponse> responseObserver) {

    try {
      CreatePostResponse response = postService.createPost(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("createPost error : ", e);

      responseObserver.onError(
          io.grpc.Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }

  @Override
  public void updatePost(UpdatePostRequest request,
      StreamObserver<UpdatePostResponse> responseObserver) {
    try {
      UpdatePostResponse response = postService.updatePost(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("updatePost error : ", e);

      responseObserver.onError(
          io.grpc.Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }

  @Override
  public void isLikedPost(IsLikedPostRequest request,
      StreamObserver<IsLikedPostResponse> responseObserver) {

    try {
      IsLikedPostResponse response = postService.isLikedPost(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("isLikedPost error : ", e);

      responseObserver.onError(
          io.grpc.Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }

  @Override
  public void deletePost(DeletePostRequest request,
      StreamObserver<DeletePostResponse> responseObserver) {
    try {
      DeletePostResponse response = postService.deletePost(request);

      deleteAllCommentsByPostProducer.sendDeleteAllCommentsByPostRequest(request.getPostId());

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("deletePost error : ", e);

      responseObserver.onError(
          io.grpc.Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }


}
