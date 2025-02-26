package org.example.gongiklifeclientbegraphql.service.user;

import com.gongik.userService.domain.service.UserServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.user.signUp.ServiceSignUpResponseDto;
import org.example.gongiklifeclientbegraphql.dto.user.signUp.SignUpUserRequestDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpService {

  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

  public ServiceSignUpResponseDto signUp(SignUpUserRequestDto requestDto) {

    return ServiceExceptionHandlingUtil.handle("SignUpService", () -> {
      return ServiceSignUpResponseDto.fromSignUpResponseProto(userBlockingStub.signUp(
          requestDto.toSignUpRequestProto()));
    });

  }
}
