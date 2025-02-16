package org.example.gongiklifeclientbegraphql.dto.community.myComments;


import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyCommentsRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCommentsRequestDto {

  private String userId;
  private String cursor;
  private Integer pageSize;

  public MyCommentsRequest toProto() {
    MyCommentsRequest.Builder builder = MyCommentsRequest.newBuilder()
        .setUserId(userId)
        .setPageSize(pageSize);

    if (cursor != null) {
      builder.setCursor(cursor);
    }

    return builder.build();
  }
}
