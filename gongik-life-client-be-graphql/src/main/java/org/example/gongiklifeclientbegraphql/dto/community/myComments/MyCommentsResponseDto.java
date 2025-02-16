package org.example.gongiklifeclientbegraphql.dto.community.myComments;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyCommentsResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gongiklifeclientbegraphql.dto.common.PageInfoDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCommentsResponseDto {

  private List<MyCommentForListDto> listComment;
  private PageInfoDto pageInfo;

  public static MyCommentsResponseDto fromProto(MyCommentsResponse myCommentsResponseProto) {
    return MyCommentsResponseDto.builder()
        .listComment(
            MyCommentForListDto.fromListCommentListProto(
                myCommentsResponseProto.getListCommentList()))
        .pageInfo(PageInfoDto.fromCommunityServiceProto(myCommentsResponseProto.getPageInfo()))
        .build();
  }

}
