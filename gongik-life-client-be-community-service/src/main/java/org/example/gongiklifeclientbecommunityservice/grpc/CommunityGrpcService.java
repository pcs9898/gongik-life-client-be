package org.example.gongiklifeclientbecommunityservice.grpc;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.gongiklifeclientbecommunityservice.producer.DeleteAllCommentsByPostProducer;
import org.example.gongiklifeclientbecommunityservice.service.CommentService;
import org.example.gongiklifeclientbecommunityservice.service.comment.*;
import org.example.gongiklifeclientbecommunityservice.service.post.*;
import util.GrpcServiceExceptionHandlingUtil;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class CommunityGrpcService extends CommunityServiceGrpc.CommunityServiceImplBase {

    private final DeleteAllCommentsByPostProducer deleteAllCommentsByPostProducer;
    private final PostService postService;
    private final CommentService commentService;
    private final CreatePostService createPostService;
    private final UpdatePostService updatePostService;
    private final DeletePostService deletePostService;
    private final GetPostService getPostService;
    private final GetPostsService getPostsService;
    private final MyPostsService myPostsService;
    private final UserPostsService userPostsService;
    private final MyLikedPostsService myLikedPostsService;
    private final SearchPostsService searchPostsService;
    private final CreateCommentService createCommentService;
    private final UpdateCommentService updateCommentService;
    private final DeleteCommentService deleteCommentService;
    private final GetCommentsService getCommentsService;
    private final MyCommentsService myCommentsService;

    @Override
    public void createPost(CreatePostRequest request,
                           StreamObserver<CreatePostResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("createPost",
                () -> createPostService.createPost(request),
                responseObserver);
    }

    @Override
    public void updatePost(UpdatePostRequest request,
                           StreamObserver<UpdatePostResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("updatePost",
                () -> updatePostService.updatePost(request),
                responseObserver);

    }

    @Override
    public void isLikedPost(IsLikedPostRequest request,
                            StreamObserver<IsLikedPostResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("isLikedPost",
                () -> postService.isLikedPost(request),
                responseObserver);
    }

    @Override
    public void deletePost(DeletePostRequest request,
                           StreamObserver<DeletePostResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("deletePost",
                () -> {
                    DeletePostResponse response = deletePostService.deletePost(request);

                    deleteAllCommentsByPostProducer.sendDeleteAllCommentsByPostRequest(request.getPostId());

                    return response;
                },
                responseObserver);
    }

    @Override
    public void getPost(GetPostRequest request, StreamObserver<GetPostResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("getPost",
                () -> getPostService.getPost(request),
                responseObserver);
    }

    @Override
    public void isLikedPostAndCommentCount(IsLikedPostAndCommentCountRequest request,
                                           StreamObserver<IsLikedPostAndCommentCountResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("isLikedPostAndCommentCount",
                () -> postService.isLikedPostAndCommentCount(request),
                responseObserver);
    }

    @Override
    public void posts(PostsRequest request, StreamObserver<PostsResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("posts",
                () -> getPostsService.posts(request),
                responseObserver);
    }

    @Override
    public void createComment(CreateCommentRequest request,
                              StreamObserver<CreateCommentResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("createComment",
                () -> createCommentService.createComment(request),
                responseObserver);
    }

    @Override
    public void updateComment(UpdateCommentRequest request,
                              StreamObserver<UpdateCommentResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("updateComment",
                () -> updateCommentService.updateComment(request),
                responseObserver);
    }

    @Override
    public void deleteComment(DeleteCommentRequest request,
                              StreamObserver<DeleteCommentResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("deleteComment",
                () -> deleteCommentService.deleteComment(request),
                responseObserver);
    }

    @Override
    public void comments(CommentsRequest request, StreamObserver<CommentsResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("comments",
                () -> getCommentsService.comments(request),
                responseObserver);
    }

    @Override
    public void myPosts(MyPostsRequest request, StreamObserver<MyPostsResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("myPosts",
                () -> myPostsService.myPosts(request),
                responseObserver);
    }

    @Override
    public void userPosts(UserPostsRequest request,
                          StreamObserver<UserPostsResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("userPosts",
                () -> userPostsService.userPosts(request),
                responseObserver);
    }

    @Override
    public void myLikedPosts(MyLikedPostsRequest request,
                             StreamObserver<MyLikedPostsResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("myLikedPosts",
                () -> myLikedPostsService.myLikedPosts(request),
                responseObserver);
    }

    @Override
    public void myComments(MyCommentsRequest request,
                           StreamObserver<MyCommentsResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("myComments",
                () -> myCommentsService.myComments(request),
                responseObserver);
    }

    @Override
    public void searchPosts(SearchPostsRequest request,
                            StreamObserver<SearchPostsResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("searchPosts",
                () -> searchPostsService.searchPosts(request),
                responseObserver);
    }

    @Override
    public void existsPost(ExistsPostRequest request,
                           StreamObserver<ExistsPostResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("existsPost",
                () -> postService.existsPost(request),
                responseObserver);
    }

    @Override
    public void existsComment(ExistsCommentRequest request,
                              StreamObserver<ExistsCommentResponse> responseObserver) {

        GrpcServiceExceptionHandlingUtil.handle("existsComment",
                () -> commentService.existsComment(request),
                responseObserver);
    }
}
