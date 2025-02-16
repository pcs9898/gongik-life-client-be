package org.example.gongiklifeclientbegraphql.dto.community.myPosts;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyPostsResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gongiklifeclientbegraphql.dto.common.PageInfoDto;
import org.example.gongiklifeclientbegraphql.dto.common.PostResponseDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPostsResponseDto {

  private List<PostResponseDto> listPost;
  private PageInfoDto pageInfo;

  public static MyPostsResponseDto fromProto(MyPostsResponse myPostsResponseProto) {
    return MyPostsResponseDto.builder()
        .listPost(PostResponseDto.fromPostsResponseProto(myPostsResponseProto.getListPostList()))
        .pageInfo(PageInfoDto.fromCommunityServiceProto(myPostsResponseProto.getPageInfo()))
        .build();
  }
}
