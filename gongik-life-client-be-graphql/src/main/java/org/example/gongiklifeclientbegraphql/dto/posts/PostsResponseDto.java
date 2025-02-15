package org.example.gongiklifeclientbegraphql.dto.posts;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostsResponse;
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
public class PostsResponseDto {

  private List<PostResponseDto> listPost;
  private PageInfoDto pageInfo;

  public static PostsResponseDto fromProto(PostsResponse postsProto) {
    return PostsResponseDto.builder()
        .listPost(PostResponseDto.fromPostsResponseProto(postsProto.getListPostList()))
        .pageInfo(PageInfoDto.fromCommunityServiceProto(postsProto.getPageInfo()))
        .build();

  }
}
