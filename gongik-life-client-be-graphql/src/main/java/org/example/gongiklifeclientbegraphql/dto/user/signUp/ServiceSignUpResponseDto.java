package org.example.gongiklifeclientbegraphql.dto.user.signUp;

import com.gongik.userService.domain.service.UserServiceOuterClass.SignUpResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceSignUpResponseDto {

  private SignUpUserDto user;
  private String accessToken;
  private String accessTokenExpiresAt;
  private String refreshToken;


  public static ServiceSignUpResponseDto fromSignUpResponseProto(SignUpResponse proto) {

    return ServiceSignUpResponseDto.builder()
        .user(SignUpUserDto.fromProto(proto.getUser()))
        .accessToken(proto.getAccessToken())
        .accessTokenExpiresAt(proto.getAccessTokenExpiresAt())
        .refreshToken(proto.getRefreshToken())
        .build();
  }

}