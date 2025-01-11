package org.example.gongiklifeclientbegraphql.dto.signUp;

import com.gongik.userService.domain.service.UserServiceOuterClass.SignUpResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponseDto {

  private SignUpUserDto user;
  private String accessToken;
  private String accessTokenExpiresAt;


  public static SignUpResponseDto fromProto(SignUpResponse proto) {
    return SignUpResponseDto.builder()
        .user(SignUpUserDto.fromProto(proto.getUser()))
        .accessToken(proto.getAccessToken())
        .accessTokenExpiresAt(proto.getAccessTokenExpiresAt())
        .build();
  }

}