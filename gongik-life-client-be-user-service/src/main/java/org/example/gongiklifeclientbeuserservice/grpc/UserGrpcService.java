package org.example.gongiklifeclientbeuserservice.grpc;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.CheckUserInstitutionRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.CheckUserInstitutionResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.FindByEmailForAuthRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.FindByEmailForAuthResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdsRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.GetUserNameByIdsResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.HasInstitutionRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.HasInstitutionResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.SignUpRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.SignUpResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.UpdateProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.UpdateProfileResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.UserProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.UserProfileResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.gongiklifeclientbeuserservice.service.MyProfileService;
import org.example.gongiklifeclientbeuserservice.service.SendEmailVerificationCodeService;
import org.example.gongiklifeclientbeuserservice.service.SignupService;
import org.example.gongiklifeclientbeuserservice.service.UserProfileService;
import org.example.gongiklifeclientbeuserservice.service.UserSerivce;
import org.example.gongiklifeclientbeuserservice.service.VerifyEmailCodeService;
import util.GrpcServiceExceptionHandlingUtil;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {


  private final UserSerivce userService;
  private final SendEmailVerificationCodeService sendEmailVerificationCodeService;
  private final VerifyEmailCodeService verifyEmailCodeService;
  private final SignupService signupService;
  private final MyProfileService myProfileService;
  private final UserProfileService userProfileService;

  @Override
  public void sendEmailVerificationCode(SendEmailVerificationCodeRequest request,
      StreamObserver<SendEmailVerificationCodeResponse> responseObserver) {
    GrpcServiceExceptionHandlingUtil.handle("sendEmailVerificationCode",
        () -> sendEmailVerificationCodeService.sendEmailVerificationCode(request),
        responseObserver);

  }

  @Override
  public void verifyEmailCode(VerifyEmailCodeRequest request,
      StreamObserver<VerifyEmailCodeResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("verifyEmailCode",
        () -> verifyEmailCodeService.verifyEmailCode(request),
        responseObserver);
  }

  @Override
  public void signUp(SignUpRequest request, StreamObserver<SignUpResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("signUp",
        () -> signupService.signUp(request),
        responseObserver);

  }

  @Override
  public void findByEmailForAuth(FindByEmailForAuthRequest request,
      StreamObserver<FindByEmailForAuthResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("findByEmailForAuth",
        () -> userService.findByEmailForAuth(request),
        responseObserver);
  }

  @Override
  public void myProfile(MyProfileRequest request,
      StreamObserver<MyProfileResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("myProfile",
        () -> myProfileService.myProfile(request),
        responseObserver);

  }

  @Override
  public void userProfile(UserProfileRequest request,
      StreamObserver<UserProfileResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("userProfile",
        () -> userProfileService.userProfile(request),
        responseObserver);
  }

  @Override
  public void updateProfile(UpdateProfileRequest request,
      StreamObserver<UpdateProfileResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("updateProfile",
        () -> userService.updateProfile(request),
        responseObserver);
  }

  @Override
  public void checkUserInstitution(CheckUserInstitutionRequest request,
      StreamObserver<CheckUserInstitutionResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("checkUserInstitution",
        () -> userService.checkUserInstitution(request),
        responseObserver);
  }

  @Override
  public void getUserNameById(GetUserNameByIdRequest request,
      StreamObserver<GetUserNameByIdResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("getUserNameById",
        () -> userService.getUserNameById(request),
        responseObserver);
  }

  @Override
  public void getUserNameByIds(GetUserNameByIdsRequest request,
      StreamObserver<GetUserNameByIdsResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("getUserNameByIds",
        () -> userService.getUserNameByIds(request),
        responseObserver);
  }

  @Override
  public void hasInstitution(HasInstitutionRequest request,
      StreamObserver<HasInstitutionResponse> responseObserver) {

    GrpcServiceExceptionHandlingUtil.handle("hasInstitution",
        () -> userService.hasInstitution(request),
        responseObserver);
  }
}
