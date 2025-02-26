package org.example.gongiklifeclientbegraphql.dto.community.myComments;


import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyCommentsRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCommentsRequestDto {

    private String userId;
    private String cursor;

    @Range(min = 1, max = 20)
    private Integer pageSize;

    public MyCommentsRequest toMyCommentsRequestProto() {
        MyCommentsRequest.Builder builder = MyCommentsRequest.newBuilder()
                .setUserId(userId)
                .setPageSize(pageSize);

        if (cursor != null) {
            builder.setCursor(cursor);
        }

        return builder.build();
    }
}
