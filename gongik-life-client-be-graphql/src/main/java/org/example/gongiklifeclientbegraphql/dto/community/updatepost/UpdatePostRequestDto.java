package org.example.gongiklifeclientbegraphql.dto.community.updatepost;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.UpdatePostRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class UpdatePostRequestDto {

  private String userId;
  private String postId;
  private String title;
  private String content;


  public UpdatePostRequest toProto() {

    UpdatePostRequest.Builder builder = UpdatePostRequest.newBuilder()
        .setUserId(userId)
        .setPostId(postId);

    if (title != null) {
      builder.setTitle(title);
    }

    if (content != null) {
      builder.setContent(content);
    }

    return builder.build();
  }
}
