package org.example.gongiklifeclientbeuserservice.grpc;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.gongiklifeclientbeuserservice.service.userSerivce;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class userGrpcService extends UserServiceGrpc.UserServiceImplBase {

  private final userSerivce userService;

  @Override
  public void sendEmailVerificationCode(SendEmailVerificationCodeRequest request,
      StreamObserver<SendEmailVerificationCodeResponse> responseObserver) {
    try {
      SendEmailVerificationCodeResponse response = userService.sendEmailVerificationCode(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("sendEmailVerificationCode error: {}",
          e.getLocalizedMessage());

      responseObserver.onError(e);
    }
  }

  @Override
  public void verifyEmailCode(VerifyEmailCodeRequest request,
      StreamObserver<VerifyEmailCodeResponse> responseObserver) {
    try {
      VerifyEmailCodeResponse response = userService.verifyEmailCode(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("verifyEmailCode error: {}",
          e.getLocalizedMessage());

      responseObserver.onError(e);

    }
  }
}
