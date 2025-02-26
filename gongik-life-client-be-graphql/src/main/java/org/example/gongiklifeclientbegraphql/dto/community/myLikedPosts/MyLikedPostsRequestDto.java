package org.example.gongiklifeclientbegraphql.dto.community.myLikedPosts;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyLikedPostsRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyLikedPostsRequestDto {


    private String userId;
    private String cursor;

    @Range(min = 1, max = 20)
    private Integer pageSize;

    public MyLikedPostsRequest toMyLikedPostsRequestProto() {
        MyLikedPostsRequest.Builder builder = MyLikedPostsRequest.newBuilder()
                .setUserId(userId)
                .setPageSize(pageSize);

        if (cursor != null) {
            builder.setCursor(cursor);
        }

        return builder.build();
    }

}
