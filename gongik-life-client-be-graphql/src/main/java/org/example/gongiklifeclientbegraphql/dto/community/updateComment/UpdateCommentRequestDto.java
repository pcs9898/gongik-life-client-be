package org.example.gongiklifeclientbegraphql.dto.community.updateComment;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdateCommentRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentRequestDto {

  private String userId;
  private String commentId;
  private String content;

  public UpdateCommentRequest toProto() {
    return UpdateCommentRequest.newBuilder()
        .setUserId(userId)
        .setCommentId(commentId)
        .setContent(content)
        .build();
  }
}
