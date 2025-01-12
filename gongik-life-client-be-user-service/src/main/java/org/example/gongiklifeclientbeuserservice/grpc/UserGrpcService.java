package org.example.gongiklifeclientbeuserservice.grpc;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.FindByEmailForAuthRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.FindByEmailForAuthResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.SignUpRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.SignUpResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.gongiklifeclientbeuserservice.service.UserSerivce;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {


  private final UserSerivce userService;

  @Override
  public void sendEmailVerificationCode(SendEmailVerificationCodeRequest request,
      StreamObserver<SendEmailVerificationCodeResponse> responseObserver) {
    try {
      SendEmailVerificationCodeResponse response = userService.sendEmailVerificationCode(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {

      log.error("sendEmailVerificationCode error: {} - {}",
          e.getMessage(), e.getLocalizedMessage());

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
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

      log.error("verifyEmailCode error: {} - {}",
          e.getMessage(), e.getLocalizedMessage());

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );

    }
  }

  @Override
  public void signUp(SignUpRequest request, StreamObserver<SignUpResponse> responseObserver) {
    try {
      SignUpResponse response = userService.signUp(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();

    } catch (Exception e) {
      log.error("signUp error: {} - {}",
          e.getMessage(), e.getLocalizedMessage());

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }

  @Override
  public void findByEmailForAuth(FindByEmailForAuthRequest request,
      StreamObserver<FindByEmailForAuthResponse> responseObserver) {
    try {
      FindByEmailForAuthResponse response = userService.findByEmailForAuth(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("findByEmailForAuth error: {} - {}",
          e.getMessage(), e.getLocalizedMessage());

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }

  @Override
  public void myProfile(MyProfileRequest request,
      StreamObserver<MyProfileResponse> responseObserver) {
    try {
      MyProfileResponse response = userService.myProfile(request);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("myProfile error: {} - {}",
          e.getMessage(), e.getLocalizedMessage());

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );


    }
  }
}
