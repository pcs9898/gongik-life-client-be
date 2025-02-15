package org.example.gongiklifeclientbecommunityservice.grpc;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreateCommentRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreateCommentResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreatePostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreatePostResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.DeletePostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.DeletePostResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.GetPostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.GetPostResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostAndCommentCountRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostAndCommentCountResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostsRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostsResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdateCommentRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdateCommentResponse;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdatePostRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdatePostResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.gongiklifeclientbecommunityservice.producer.DeleteAllCommentsByPostProducer;
import org.example.gongiklifeclientbecommunityservice.service.CommentService;
import org.example.gongiklifeclientbecommunityservice.service.PostService;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class CommunityGrpcService extends CommunityServiceGrpc.CommunityServiceImplBase {

  private final DeleteAllCommentsByPostProducer deleteAllCommentsByPostProducer;
  private final PostService postService;
  private final CommentService commentService;

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

  @Override
  public void getPost(GetPostRequest request, StreamObserver<GetPostResponse> responseObserver) {
    try {
      GetPostResponse response = postService.getPost(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("getPost error : ", e);

      responseObserver.onError(
          io.grpc.Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }

  @Override
  public void isLikedPostAndCommentCount(IsLikedPostAndCommentCountRequest request,
      StreamObserver<IsLikedPostAndCommentCountResponse> responseObserver) {
    try {
      IsLikedPostAndCommentCountResponse response = postService.isLikedPostAndCommentCount(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("isLikedPostAndCommentCount error : ", e);

      responseObserver.onError(
          io.grpc.Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }

  @Override
  public void posts(PostsRequest request, StreamObserver<PostsResponse> responseObserver) {
    try {
      PostsResponse response = postService.posts(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("posts error : ", e);

      responseObserver.onError(
          io.grpc.Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }

  @Override
  public void createComment(CreateCommentRequest request,
      StreamObserver<CreateCommentResponse> responseObserver) {
    try {
      CreateCommentResponse response = commentService.createComment(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("createComment error : ", e);

      responseObserver.onError(
          io.grpc.Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }
<<<<<<< Updated upstream
=======
<<<<<<< HEAD
=======
>>>>>>> Stashed changes

  @Override
  public void updateComment(UpdateCommentRequest request,
      StreamObserver<UpdateCommentResponse> responseObserver) {
    try {
      UpdateCommentResponse response = commentService.updateComment(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("updateComment error : ", e);

      responseObserver.onError(
          io.grpc.Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }
<<<<<<< Updated upstream
=======
>>>>>>> feature/29updateComment
>>>>>>> Stashed changes
}
