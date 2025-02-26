package org.example.gongiklifeclientbegraphql.dto.community.posts;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostsRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostsRequestDto {

    private String userId;

    @Range(min = 1, max = 7)
    private Integer postCategoryId;

    private String cursor;

    @Range(min = 1, max = 20)
    private Integer pageSize;

    public PostsRequest toPostsRequestProto() {
        PostsRequest.Builder builder = PostsRequest.newBuilder()
                .setPostCategoryId(postCategoryId)
                .setPageSize(pageSize);

        if (userId != null) {
            builder.setUserId(userId);
        }

        if (cursor != null) {
            builder.setCursor(cursor);
        }

        return builder.build();
    }
}
