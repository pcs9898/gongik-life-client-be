package org.example.gongiklifeclientbegraphql.dto.community.myLikedPosts;


import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyLikedPostsResponse;
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
public class MyLikedPostsResponseDto {

  private List<PostResponseDto> listPost;
  private PageInfoDto pageInfo;

  public static MyLikedPostsResponseDto fromProto(MyLikedPostsResponse myLikedPostsResponseProto) {
    return MyLikedPostsResponseDto.builder()
        .listPost(
            PostResponseDto.fromPostsResponseProto(myLikedPostsResponseProto.getListPostList()))
        .pageInfo(PageInfoDto.fromCommunityServiceProto(myLikedPostsResponseProto.getPageInfo()))
        .build();
  }
}
