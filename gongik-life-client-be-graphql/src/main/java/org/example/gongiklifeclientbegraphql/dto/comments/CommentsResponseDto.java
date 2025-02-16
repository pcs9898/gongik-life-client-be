package org.example.gongiklifeclientbegraphql.dto.comments;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.CommentsResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gongiklifeclientbegraphql.dto.createComment.CommentForListDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentsResponseDto {

  private List<CommentForListDto> listComment;

  public static CommentsResponseDto fromProto(CommentsResponse comments) {
    return CommentsResponseDto.builder()
        .listComment(CommentForListDto.fromProtoList(comments.getListCommentList()))
        .build();
  }
}
