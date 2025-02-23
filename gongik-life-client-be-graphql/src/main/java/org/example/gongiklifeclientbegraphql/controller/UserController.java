package org.example.gongiklifeclientbegraphql.controller;

import dto.user.UserLoginHistoryRequestDto;
import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.user.me.MyProfileResponseDto;
import org.example.gongiklifeclientbegraphql.dto.user.sendEmailVerificationCode.SendEmailVerificationCodeRequestDto;
import org.example.gongiklifeclientbegraphql.dto.user.sendEmailVerificationCode.SendEmailVerificationCodeResponseDto;
import org.example.gongiklifeclientbegraphql.dto.user.signUp.ServiceSignUpResponseDto;
import org.example.gongiklifeclientbegraphql.dto.user.signUp.SignUpResponseDto;
import org.example.gongiklifeclientbegraphql.dto.user.signUp.SignUpUserRequestDto;
import org.example.gongiklifeclientbegraphql.dto.user.updateProfile.UpdateProfileRequestDto;
import org.example.gongiklifeclientbegraphql.dto.user.updateProfile.UpdateProfileResponseDto;
import org.example.gongiklifeclientbegraphql.dto.user.userProfile.UserProfileRequestDto;
import org.example.gongiklifeclientbegraphql.dto.user.userProfile.UserProfileResponseDto;
import org.example.gongiklifeclientbegraphql.dto.user.verifyEmailCode.VerifyEmailCodeRequestDto;
import org.example.gongiklifeclientbegraphql.dto.user.verifyEmailCode.VerifyEmailCodeResponseDto;
import org.example.gongiklifeclientbegraphql.service.UserService;
import org.example.gongiklifeclientbegraphql.service.user.MyProfileService;
import org.example.gongiklifeclientbegraphql.service.user.SendEmailVerificationCodeService;
import org.example.gongiklifeclientbegraphql.service.user.SignUpService;
import org.example.gongiklifeclientbegraphql.service.user.UserProfileService;
import org.example.gongiklifeclientbegraphql.service.user.VerifyEmailCodeService;
import org.example.gongiklifeclientbegraphql.util.ControllerExceptionHandlingUtil;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.Arguments;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {


  private final UserService userService;
  private final SendEmailVerificationCodeService sendEmailVerificationCodeService;
  private final VerifyEmailCodeService verifyEmailCodeService;
  private final SignUpService signUpService;
  private final MyProfileService myProfileService;
  private final UserProfileService userProfileService;

  @MutationMapping
  public SendEmailVerificationCodeResponseDto sendEmailVerificationCode(
      @Argument("sendEmailVerificationCodeInput") @Valid SendEmailVerificationCodeRequestDto requestDto) {

    return ControllerExceptionHandlingUtil.handle(
        () -> sendEmailVerificationCodeService.sendEmailVerificationCode(requestDto));

  }

  @MutationMapping
  public VerifyEmailCodeResponseDto verifyEmailCode(
      @Argument("verifyEmailCodeInput") @Valid VerifyEmailCodeRequestDto requestDto) {

    return ControllerExceptionHandlingUtil.handle(
        () -> verifyEmailCodeService.verifyEmailCode(requestDto));

  }


  @MutationMapping
  public SignUpResponseDto signUp(
      @Argument("signUpInput") SignUpUserRequestDto requestDto,
      GraphQLContext context,
      @ContextValue(name = "request") HttpServletRequest request
  ) {

    return ControllerExceptionHandlingUtil.handle(
        () -> {
          ServiceSignUpResponseDto serviceSignUpResponse = signUpService.signUp(requestDto);
          context.put("refreshToken", serviceSignUpResponse.getRefreshToken());

          String ipAddress = getClientIpAddress(request);
          sendUserLoginHistory(serviceSignUpResponse.getUser().getId(), ipAddress);

          return buildSignUpResponse(serviceSignUpResponse);
        });
  }

  @QueryMapping
  public MyProfileResponseDto myProfile(DataFetchingEnvironment dataFetchingEnvironment) {

    return ControllerExceptionHandlingUtil.handle(
        () -> {
          String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

          return myProfileService.myProfile(userId);
        });
  }

  @QueryMapping
  public UserProfileResponseDto userProfile(
      @Argument("userProfileInput") UserProfileRequestDto requestDto
  ) {
    return ControllerExceptionHandlingUtil.handle(
        () -> userProfileService.userProfile(requestDto.getUserId()));

  }

  @MutationMapping
  public UpdateProfileResponseDto updateProfile(
      @Arguments UpdateProfileRequestDto requestDto,
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    return ControllerExceptionHandlingUtil.handle(
        () -> {
          String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

          requestDto.setUserId(userId);

          return userService.updateProfile(requestDto);
        });
  }


  private String getClientIpAddress(HttpServletRequest request) {
    String ipAddress = request.getHeader("X-Forwarded-For");
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getRemoteAddr();
    }
    return ipAddress;
  }

  private void sendUserLoginHistory(String userId, String ipAddress) {
    UserLoginHistoryRequestDto userLoginHistoryRequestDto = UserLoginHistoryRequestDto.builder()
        .userId(userId)
        .ipAddress(ipAddress)
        .build();
    userService.sendUserLoginHistoryRequest(userLoginHistoryRequestDto);
  }

  private SignUpResponseDto buildSignUpResponse(ServiceSignUpResponseDto serviceSignUpResponse) {
    return SignUpResponseDto.builder()
        .user(serviceSignUpResponse.getUser())
        .accessToken(serviceSignUpResponse.getAccessToken())
        .accessTokenExpiresAt(serviceSignUpResponse.getAccessTokenExpiresAt())
        .build();
  }

}
