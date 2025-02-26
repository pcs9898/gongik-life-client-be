package org.example.gongiklifeclientbegraphql.dto.community.userPosts;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UserPostsResponse;
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
public class UserPostsResponseDto {

    private List<PostResponseDto> listPost;
    private PageInfoDto pageInfo;

    public static UserPostsResponseDto fromUserPostsResponseProto(UserPostsResponse userPostsResponseProto) {
        return UserPostsResponseDto.builder()
                .listPost(PostResponseDto.fromPostsResponseProto(userPostsResponseProto.getListPostList()))
                .pageInfo(PageInfoDto.fromCommunityServiceProto(userPostsResponseProto.getPageInfo()))
                .build();
    }
}
