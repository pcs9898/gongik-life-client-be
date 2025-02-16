package org.example.gongiklifeclientbegraphql.dto.community.updateComment;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdateCommentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentResponseDto {

  private String id;
  private Boolean success;

  public static UpdateCommentResponseDto fromProto(UpdateCommentResponse comment) {
    return UpdateCommentResponseDto.builder()
        .id(comment.getId())
        .success(comment.getSuccess())
        .build();
  }
}
