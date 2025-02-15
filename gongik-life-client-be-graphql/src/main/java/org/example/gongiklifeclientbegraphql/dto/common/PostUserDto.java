package org.example.gongiklifeclientbegraphql.dto.common;

import com.gongik.communityService.domain.service.CommunityServiceOuterClass.PostUser;
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
public class PostUserDto {

  private String userId;
  private String userName;

  public static PostUserDto fromProto(PostUser user) {
    return PostUserDto.builder()
        .userId(user.getUserId())
        .userName(user.getUserName())
        .build();
  }
}
