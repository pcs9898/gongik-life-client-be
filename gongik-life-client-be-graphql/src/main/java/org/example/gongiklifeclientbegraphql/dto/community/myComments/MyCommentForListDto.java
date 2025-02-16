package org.example.gongiklifeclientbegraphql.dto.community.myComments;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyCommentForList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gongiklifeclientbegraphql.dto.common.PostShortInfoDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCommentForListDto {

  private String id;
  private PostShortInfoDto post;
  private String content;
  private String createdAt;

  public static List<MyCommentForListDto> fromListCommentListProto(
      List<MyCommentForList> listCommentlist) {
    return listCommentlist.stream()
        .map(comment -> MyCommentForListDto.builder()
            .id(comment.getId())
            .post(PostShortInfoDto.fromProto(comment.getPost()))
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .build()).toList();
  }

}
