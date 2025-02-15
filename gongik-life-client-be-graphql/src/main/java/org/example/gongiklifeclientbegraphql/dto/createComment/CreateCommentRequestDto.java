package org.example.gongiklifeclientbegraphql.dto.createComment;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CreateCommentRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequestDto {

  private String userId;
  private String postId;
  private String parentCommentId;
  private String content;

  public CreateCommentRequest toProto() {
    CreateCommentRequest.Builder builder = CreateCommentRequest.newBuilder()
        .setUserId(userId)
        .setPostId(postId)
        .setContent(content);

    if (parentCommentId != null) {
      builder.setParentCommentId(parentCommentId);
    }

    return builder.build();
  }
}
