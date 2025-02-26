package org.example.gongiklifeclientbegraphql.dto.community.searchPosts;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.SearchPostsRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchPostsRequestDto {

    private String userId;

    @NotBlank
    private String searchKeyword;

    @Range(min = 1, max = 7)
    private Integer postCategoryId;

    private String cursor;

    @Range(min = 1, max = 20)
    private Integer pageSize;

    public SearchPostsRequest toSearchPostsRequestProto() {
        SearchPostsRequest.Builder builder = SearchPostsRequest.newBuilder()
                .setSearchKeyword(searchKeyword)
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
