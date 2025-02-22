package org.example.gongiklifeclientbegraphql.service.user;

import com.gongik.userService.domain.service.UserServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.user.sendEmailVerificationCode.SendEmailVerificationCodeRequestDto;
import org.example.gongiklifeclientbegraphql.dto.user.sendEmailVerificationCode.SendEmailVerificationCodeResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendEmailVerificationCodeService {

  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

  public SendEmailVerificationCodeResponseDto sendEmailVerificationCode(
      SendEmailVerificationCodeRequestDto requestDto) {
    
    return ServiceExceptionHandlingUtil.handle("SendEmailVerificationCodeService", () -> {
      Assert.notNull(requestDto, "requestDto must not be null");

      return SendEmailVerificationCodeResponseDto.fromSendEmailVerificationCodeResponseProto(
          userBlockingStub.sendEmailVerificationCode(
              requestDto.toSendEmailVerificationCodeRequestProto()));
    });
  }
}
