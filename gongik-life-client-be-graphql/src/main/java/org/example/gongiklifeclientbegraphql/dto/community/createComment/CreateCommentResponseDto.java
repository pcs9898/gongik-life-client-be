package org.example.gongiklifeclientbegraphql.dto.community.createComment;


import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreateCommentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gongiklifeclientbegraphql.dto.common.PostUserDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentResponseDto {

  private String id;
  private PostUserDto user;
  private String postId;
  private String parentCommentId;
  private String content;
  private String createdAt;

  public static CreateCommentResponseDto fromProto(CreateCommentResponse comment) {
    return CreateCommentResponseDto.builder()
        .id(comment.getId())
        .user(PostUserDto.fromProto(comment.getUser()))
        .postId(comment.getPostId())
        .parentCommentId(
            comment.getParentCommentId().isEmpty() ? null : comment.getParentCommentId())
        .content(comment.getContent())
        .createdAt(comment.getCreatedAt())
        .build();
  }
}
