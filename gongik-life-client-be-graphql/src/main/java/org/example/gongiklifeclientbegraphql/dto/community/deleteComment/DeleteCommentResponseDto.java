package org.example.gongiklifeclientbegraphql.dto.community.deleteComment;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.DeleteCommentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCommentResponseDto {

  private String commentId;
  private Boolean success;

  public static DeleteCommentResponseDto fromProto(DeleteCommentResponse deleteCommentResponse) {
    return DeleteCommentResponseDto.builder()
        .commentId(deleteCommentResponse.getCommentId())
        .success(deleteCommentResponse.getSuccess())
        .build();
  }
}
