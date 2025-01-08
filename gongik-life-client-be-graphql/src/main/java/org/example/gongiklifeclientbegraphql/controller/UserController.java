package org.example.gongiklifeclientbegraphql.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.sendEmailVerificationCode.sendEmailVerificationCodeRequestDto;
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
      @Arguments sendEmailVerificationCodeRequestDto requestDto) {

    return userService.sendEmailVerificationCode(requestDto);
  }

}
