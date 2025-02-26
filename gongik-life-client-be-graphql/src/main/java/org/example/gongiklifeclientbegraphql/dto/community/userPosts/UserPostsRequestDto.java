package org.example.gongiklifeclientbegraphql.dto.community.userPosts;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UserPostsRequest;
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
public class UserPostsRequestDto {

    private String myUserId;

    @NotBlank
    private String userId;

    private String cursor;

    @Range(min = 1, max = 20)
    private Integer pageSize;

    public UserPostsRequest toUserPostsRequestProto() {
        UserPostsRequest.Builder builder = UserPostsRequest.newBuilder()
                .setUserId(userId)
                .setPageSize(pageSize);

        if (myUserId != null) {
            builder.setMyUserId(myUserId);
        }

        if (cursor != null) {
            builder.setCursor(cursor);
        }

        return builder.build();
    }

}
