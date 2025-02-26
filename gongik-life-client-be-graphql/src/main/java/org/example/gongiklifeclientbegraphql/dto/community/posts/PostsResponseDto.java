package org.example.gongiklifeclientbegraphql.dto.community.posts;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostsResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.gongiklifeclientbegraphql.dto.common.PageInfoDto;
import org.example.gongiklifeclientbegraphql.dto.common.PostResponseDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostsResponseDto {

    private List<PostResponseDto> listPost;
    private PageInfoDto pageInfo;

    public static PostsResponseDto fromPostsResponseProto(PostsResponse postsProto) {
        return PostsResponseDto.builder()
                .listPost(PostResponseDto.fromPostsResponseProto(postsProto.getListPostList()))
                .pageInfo(PageInfoDto.fromCommunityServiceProto(postsProto.getPageInfo()))
                .build();

    }
}
