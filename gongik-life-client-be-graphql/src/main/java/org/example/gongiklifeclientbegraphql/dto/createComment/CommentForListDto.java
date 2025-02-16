package org.example.gongiklifeclientbegraphql.dto.createComment;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.common.PostUserDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class CommentForListDto {

  private String id;
  private PostUserDto user;
  private String postId;
  private String parentCommentId;
  private String content;
  private String createdAt;
  private List<CommentForListDto> childComments;

  public static List<CommentForListDto> fromProtoList(

      List<CommunityServiceOuterClass.CommentForList> listCommentList) {
    log.info("listCommentList: {}", listCommentList);
    return listCommentList.stream()
        .map(CommentForListDto::fromProto)
        .toList();
  }

  public static CommentForListDto fromProto(CommunityServiceOuterClass.CommentForList comment) {
    return CommentForListDto.builder()
        .id(comment.getId())
        .user(PostUserDto.fromProto(comment.getUser()))
        .postId(comment.getPostId())
        .parentCommentId(
            comment.getParentCommentId().isEmpty() ? null : comment.getParentCommentId())
        .content(comment.getContent())
        .createdAt(comment.getCreatedAt())
        .childComments(fromProtoList(
            comment.getChildCommentsList().isEmpty() ? null : comment.getChildCommentsList()))
        .build();
  }
}
