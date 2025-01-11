package org.example.gongiklifeclientbegraphql.controller;

import graphql.GraphQLContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.sendEmailVerificationCode.SendEmailVerificationCodeRequestDto;
import org.example.gongiklifeclientbegraphql.dto.signUp.ServiceSignUpResponseDto;
import org.example.gongiklifeclientbegraphql.dto.signUp.SignUpResponseDto;
import org.example.gongiklifeclientbegraphql.dto.signUp.SignUpUserRequestDto;
import org.example.gongiklifeclientbegraphql.dto.verifyEmailCode.VerifyEmailCodeRequestDto;
import org.example.gongiklifeclientbegraphql.service.UserService;
import org.springframework.graphql.data.method.annotation.Arguments;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final UserService userService;

  @MutationMapping
  public boolean sendEmailVerificationCode(
      @Arguments SendEmailVerificationCodeRequestDto requestDto) {

    return userService.sendEmailVerificationCode(requestDto);
  }

  @MutationMapping
  public boolean verifyEmailCode(
      @Arguments VerifyEmailCodeRequestDto requestDto) {

    return userService.verifyEmailCode(requestDto);
  }


  @MutationMapping
  public SignUpResponseDto signUp(
      @Arguments SignUpUserRequestDto requestDto,
      GraphQLContext context
  ) {
    ServiceSignUpResponseDto serviceSignUpResponse = userService.signUp(requestDto);

    context.put("refreshToken", serviceSignUpResponse.getRefreshToken());

    return SignUpResponseDto.builder()
        .user(serviceSignUpResponse.getUser())
        .accessToken(serviceSignUpResponse.getAccessToken())
        .accessTokenExpiresAt(serviceSignUpResponse.getAccessTokenExpiresAt())
        .build();
  }


}
