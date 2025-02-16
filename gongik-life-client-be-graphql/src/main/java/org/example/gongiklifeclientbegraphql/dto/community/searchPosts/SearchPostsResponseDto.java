package org.example.gongiklifeclientbegraphql.dto.community.searchPosts;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.SearchPostsResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.common.PageInfoDto;
import org.example.gongiklifeclientbegraphql.dto.common.PostResponseDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class SearchPostsResponseDto {


  private List<PostResponseDto> listPost;
  private PageInfoDto pageInfo;

  public static SearchPostsResponseDto fromProto(SearchPostsResponse searchPostsProto) {
    log.info("searchPostsProto: {}", searchPostsProto);
    return SearchPostsResponseDto.builder()
        .listPost(PostResponseDto.fromPostsResponseProto(searchPostsProto.getListPostList()))
        .pageInfo(PageInfoDto.fromCommunityServiceProto(searchPostsProto.getPageInfo()))
        .build();
  }

}
