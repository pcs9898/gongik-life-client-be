package org.example.gongiklifeclientbegraphql.service.user;

import com.gongik.userService.domain.service.UserServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.user.verifyEmailCode.VerifyEmailCodeRequestDto;
import org.example.gongiklifeclientbegraphql.dto.user.verifyEmailCode.VerifyEmailCodeResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VerifyEmailCodeService {

  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

  public VerifyEmailCodeResponseDto verifyEmailCode(VerifyEmailCodeRequestDto requestDto) {

    return ServiceExceptionHandlingUtil.handle("UserService",
        () -> VerifyEmailCodeResponseDto.fromVerifyEmailCodeResponseProto(
            userBlockingStub.verifyEmailCode(
                requestDto.toVerifyEmailCodeRequestProto()
            )
        ));
  }
}
