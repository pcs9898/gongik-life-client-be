package org.example.gongiklifeclientbegraphql.service;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.sendEmailVerificationCode.sendEmailVerificationCodeRequestDto;
import org.example.gongiklifeclientbegraphql.dto.verifyEmailCode.verifyEmailCodeRequestDto;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

  public boolean sendEmailVerificationCode(sendEmailVerificationCodeRequestDto requestDto) {
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

  public boolean verifyEmailCode(verifyEmailCodeRequestDto requestDto) {
    try {
      VerifyEmailCodeResponse response = userBlockingStub.verifyEmailCode(
          requestDto.toProto());

      return response.getSuccess();
    } catch (Exception e) {
      log.error("gRPC 호출 중 알 수 없는 오류 발생: ", e);
      throw e;

    }

  }

}
