package org.example.gongiklifeclientbegraphql.dto.community.myPosts;


import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyPostsRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPostsRequestDto {

    private String userId;
    private String cursor;

    @Range(min = 1, max = 20)
    private Integer pageSize;

    public MyPostsRequest toMyPostsRequestProto() {
        MyPostsRequest.Builder builder = MyPostsRequest.newBuilder()
                .setUserId(userId)
                .setPageSize(pageSize);

        if (cursor != null) {
            builder.setCursor(cursor);
        }

        return builder.build();
    }

}
