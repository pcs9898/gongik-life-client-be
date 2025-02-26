package org.example.gongiklifeclientbegraphql.controller;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.IsLikedPostAndCommentCountResponse;
import dto.community.LikePostRequestDto;
import dto.community.UnLikePostRequestDto;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.common.PostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.comments.CommentsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.comments.CommentsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.createComment.CreateCommentRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.createComment.CreateCommentResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.createPost.CreatePostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.deleteComment.DeleteCommentRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.deleteComment.DeleteCommentResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.deletePost.DeletePostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.deletePost.DeletePostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.likePost.LikePostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.myComments.MyCommentsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.myComments.MyCommentsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.myLikedPosts.MyLikedPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.myLikedPosts.MyLikedPostsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.myPosts.MyPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.myPosts.MyPostsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.post.PostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.posts.PostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.posts.PostsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.searchPosts.SearchPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.searchPosts.SearchPostsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.unLikePost.UnLikePostResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.updateComment.UpdateCommentRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.updateComment.UpdateCommentResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.updatepost.UpdatePostRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.userPosts.UserPostsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.userPosts.UserPostsResponseDto;
import org.example.gongiklifeclientbegraphql.service.community.*;
import org.example.gongiklifeclientbegraphql.util.ControllerExceptionHandlingUtil;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CommunityController {

    private final CommunityService communityService;
    private final CommunityCacheService communityCacheService;
    private final CreatePostService createPostService;
    private final LikePostService likePostService;
    private final UnLikePostService unLikePostService;
    private final PostsService postsService;
    private final CreateCommentService createCommentService;
    private final UpdateCommentService updateCommentService;
    private final DeleteCommentService deleteCommentService;
    private final CommentsService commentsService;
    private final MyPostsService myPostsService;
    private final UserPostsService userPostsService;
    private final MyLikedPostsService myLikedPostsService;
    private final MyCommentsService myCommentsService;
    private final SearchPostsService searchPostsService;

    @MutationMapping
    public PostResponseDto createPost(
            @Argument("createPostInput") @Valid CreatePostRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            return createPostService.createPost(requestDto);
        });
    }

    @MutationMapping
    public PostResponseDto updatePost(
            @Argument("updatePostInput") @Valid UpdatePostRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            PostResponseDto updatedPostResponse = communityCacheService.updatePost(requestDto);

            Boolean isLiked = communityService.isLikedPost(requestDto.getPostId(), userId);

            updatedPostResponse.setIsLiked(isLiked);

            return updatedPostResponse;
        });
    }

    @MutationMapping
    public DeletePostResponseDto deletePost(
            @Argument("deletePostInput") @Valid DeletePostRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            return communityCacheService.deletePost(requestDto);
        });
    }

    @MutationMapping()
    public LikePostResponseDto likePost(
            @Argument("likePostInput") LikePostRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            return likePostService.likePost(requestDto);
        });
    }

    @MutationMapping
    public UnLikePostResponseDto unLikePost(
            @Argument("unLikePostInput") UnLikePostRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            return unLikePostService.unLikePost(requestDto);
        });
    }

    @QueryMapping
    public PostResponseDto post(
            @Argument("postInput") @Valid PostRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            if (!"-1".equals(userId)) {
                requestDto.setUserId(userId);
            }

            PostResponseDto postResponse = communityCacheService.getPost(requestDto.getPostId());

            IsLikedPostAndCommentCountResponse isLikedPostAndCommentCountResponse = communityService.isLikedPostAndCommentCount(
                    requestDto);

            postResponse.setIsLiked(isLikedPostAndCommentCountResponse.getIsLiked());

            postResponse.setCommentCount(isLikedPostAndCommentCountResponse.getCommentCount());

            return postResponse;
        });
    }

    @QueryMapping
    public PostsResponseDto posts(
            @Argument("postsFilter") @Valid PostsRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            if (!"-1".equals(userId)) {
                requestDto.setUserId(userId);
            }

            return postsService.posts(requestDto);
        });
    }

    @MutationMapping
    public CreateCommentResponseDto createComment(
            @Argument("createCommentInput") @Valid CreateCommentRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            return createCommentService.createComment(requestDto);
        });
    }


    @MutationMapping
    public UpdateCommentResponseDto updateComment(
            @Argument("updateCommentInput") @Valid UpdateCommentRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            return updateCommentService.updateComment(requestDto);
        });
    }

    @MutationMapping
    public DeleteCommentResponseDto deleteComment(
            @Argument("deleteCommentInput") @Valid DeleteCommentRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            return deleteCommentService.deleteComment(requestDto);
        });
    }


    @QueryMapping
    public CommentsResponseDto comments(
            @Argument("commentsInput") @Valid CommentsRequestDto requestDto
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            return commentsService.comments(requestDto);
        });
    }

    @QueryMapping
    public MyPostsResponseDto myPosts(
            @Argument("myPostsFilter") @Valid MyPostsRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            return myPostsService.myPosts(requestDto);
        });
    }

    @QueryMapping
    public UserPostsResponseDto userPosts(
            @Argument("userPostsFilter") @Valid UserPostsRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String myUserId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setMyUserId(myUserId);

            return userPostsService.userPosts(requestDto);
        });
    }

    @QueryMapping
    public MyLikedPostsResponseDto myLikedPosts(
            @Argument("myLikedPostsFilter") @Valid MyLikedPostsRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            return myLikedPostsService.myLikedPosts(requestDto);
        });
    }

    @QueryMapping
    public MyCommentsResponseDto myComments(
            @Argument("myCommentsFilter") @Valid MyCommentsRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            requestDto.setUserId(userId);

            return myCommentsService.myComments(requestDto);
        });
    }

    @QueryMapping
    public SearchPostsResponseDto searchPosts(
            @Argument("searchPostsFilter") @Valid SearchPostsRequestDto requestDto,
            DataFetchingEnvironment dataFetchingEnvironment
    ) {

        return ControllerExceptionHandlingUtil.handle(() -> {
            String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

            if (!"-1".equals(userId)) {
                requestDto.setUserId(userId);
            }

            return searchPostsService.searchPosts(requestDto);
        });
    }
}
