package org.example.gongiklifeclientbegraphql.dto.common;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostShortInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostShortInfoDto {

  private String postId;
  private String postTitle;

  public static PostShortInfoDto fromProto(PostShortInfo postShortInfo) {
    return PostShortInfoDto.builder()
        .postId(postShortInfo.getPostId())
        .postTitle(postShortInfo.getPostTitle())
        .build();
  }
}
