package org.example.gongiklifeclientbegraphql.service;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.UpdateProfileResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.UserProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.UserProfileResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.myProfile.MyProfileResponseDto;
import org.example.gongiklifeclientbegraphql.dto.sendEmailVerificationCode.SendEmailVerificationCodeRequestDto;
import org.example.gongiklifeclientbegraphql.dto.signUp.ServiceSignUpResponseDto;
import org.example.gongiklifeclientbegraphql.dto.signUp.SignUpUserRequestDto;
import org.example.gongiklifeclientbegraphql.dto.updateProfile.UpdateProfileRequestDto;
import org.example.gongiklifeclientbegraphql.dto.updateProfile.UpdateProfileResponseDto;
import org.example.gongiklifeclientbegraphql.dto.userProfile.UserProfileResponseDto;
import org.example.gongiklifeclientbegraphql.dto.verifyEmailCode.VerifyEmailCodeRequestDto;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

  public boolean sendEmailVerificationCode(SendEmailVerificationCodeRequestDto requestDto) {
    try {
      SendEmailVerificationCodeResponse resopnse = userBlockingStub.sendEmailVerificationCode(
          requestDto.toProto());
      return resopnse.getSuccess();
    } catch (Exception e) {
      if (e.getCause() instanceof StatusRuntimeException statusEx) {
        Status status = statusEx.getStatus();
        String description = status.getDescription();
        log.error("gRPC 호출 중 오류 발생: {}, Description: {}", status, description);
        throw new RuntimeException("서비스 호출 실패: " + description, e);
      } else {
        log.error("gRPC 호출 중 알 수 없는 오류 발생: ", e);
        throw e;
      }
    }
  }

  public boolean verifyEmailCode(VerifyEmailCodeRequestDto requestDto) {
    try {
      VerifyEmailCodeResponse response = userBlockingStub.verifyEmailCode(
          requestDto.toProto());

      return response.getSuccess();
    } catch (Exception e) {
      log.error("gRPC 호출 중 알 수 없는 오류 발생: ", e);
      throw e;

    }

  }

  public ServiceSignUpResponseDto signUp(SignUpUserRequestDto requestDto) {
    try {
      log.info("institutionId: {}", requestDto.getInstitutionId());
      com.gongik.userService.domain.service.UserServiceOuterClass.SignUpResponse response = userBlockingStub.signUp(
          requestDto.toProto());

      return ServiceSignUpResponseDto.fromProto(response);
    } catch (Exception e) {
      log.error("gRPC 호출 중 알 수 없는 오류 발생: ", e);
      throw e;
    }
  }

  public MyProfileResponseDto myProfile(String userId) {
    try {
      MyProfileResponse response = userBlockingStub.myProfile(
          MyProfileRequest.newBuilder()
              .setUserId(userId)
              .build()
      );

      return MyProfileResponseDto.fromProto(response);

    } catch (Exception e) {
      log.error("gRPC 호출 중 알 수 없는 오류 발생: ", e);
      throw e;
    }
  }

  public UserProfileResponseDto userProfile(String userId) {
    try {
      UserProfileResponse response = userBlockingStub.userProfile(
          UserProfileRequest.newBuilder()
              .setUserId(userId)
              .build()
      );

      return UserProfileResponseDto.fromProto(response);
    } catch (Exception e) {
      log.error("gRPC 호출 중 알 수 없는 오류 발생: ", e);
      throw e;
    }
  }

  public UpdateProfileResponseDto updateProfile(UpdateProfileRequestDto requestDto) {
    try {
      UpdateProfileResponse response = userBlockingStub.updateProfile(
          requestDto.toProto()
      );

      return UpdateProfileResponseDto.fromProto(response);
    } catch (Exception e) {
      log.error("gRPC 호출 중 알 수 없는 오류 발생: ", e);
      throw e;
    }
  }
}
