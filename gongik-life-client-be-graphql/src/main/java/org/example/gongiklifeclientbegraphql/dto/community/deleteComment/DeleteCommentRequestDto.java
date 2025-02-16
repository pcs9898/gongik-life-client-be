package org.example.gongiklifeclientbegraphql.dto.community.deleteComment;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.DeleteCommentRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCommentRequestDto {

  private String userId;
  private String commentId;

  public DeleteCommentRequest toProto() {
    return DeleteCommentRequest.newBuilder()
        .setUserId(userId)
        .setCommentId(commentId)
        .build();
  }
}
