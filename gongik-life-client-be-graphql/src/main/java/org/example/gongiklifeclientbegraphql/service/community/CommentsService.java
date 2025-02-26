package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.common.PostUserDto;
import org.example.gongiklifeclientbegraphql.dto.community.comments.CommentsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.comments.CommentsResponseDto;
import org.example.gongiklifeclientbegraphql.dto.community.createComment.CommentForListDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentsService {

    @GrpcClient("gongik-life-client-be-community-service")
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    public CommentsResponseDto comments(CommentsRequestDto requestDto) {

        return ServiceExceptionHandlingUtil.handle("CommentsService",
                () -> convertCommentsGrpcResponse(
                        communityServiceBlockingStub.comments(requestDto.toCommentsRequestProto())
                ));
    }

    private CommentsResponseDto convertCommentsGrpcResponse(CommunityServiceOuterClass.CommentsResponse grpcResponse) {
        CommentsResponseDto responseDto = new CommentsResponseDto();
        List<CommentForListDto> listComment = grpcResponse.getListCommentList()
                .stream()
                .map(this::convertCommentForList)
                .collect(Collectors.toList());
        responseDto.setListComment(listComment);
        return responseDto;
    }

    private CommentForListDto convertCommentForList(CommunityServiceOuterClass.CommentForList grpcComment) {
        CommentForListDto dto = new CommentForListDto();
        dto.setId(grpcComment.getId());
        dto.setPostId(grpcComment.getPostId());
        dto.setContent(grpcComment.getContent());
        dto.setCreatedAt(grpcComment.getCreatedAt());

        if (grpcComment.hasParentCommentId() && !grpcComment.getParentCommentId().isEmpty()) {
            dto.setParentCommentId(grpcComment.getParentCommentId());
        }

// 작성자 정보 매핑
        CommunityServiceOuterClass.PostUser grpcUser = grpcComment.getUser();
        PostUserDto userDto = new PostUserDto();
        userDto.setUserId(grpcUser.getUserId());
        userDto.setUserName(grpcUser.getUserName());
        dto.setUser(userDto);

        List<CommentForListDto> childComments = grpcComment.getChildCommentsList()
                .stream()
                .map(this::convertCommentForList)
                .collect(Collectors.toList());
        dto.setChildComments(childComments);

        return dto;
    }

}
