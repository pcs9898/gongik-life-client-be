package org.example.gongiklifeclientbegraphql.service;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.HasInstitutionRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.UpdateProfileResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.UserProfileRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.UserProfileResponse;
import dto.user.UserLoginHistoryRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.user.me.MyProfileResponseDto;
import org.example.gongiklifeclientbegraphql.dto.user.updateProfile.UpdateProfileRequestDto;
import org.example.gongiklifeclientbegraphql.dto.user.updateProfile.UpdateProfileResponseDto;
import org.example.gongiklifeclientbegraphql.dto.user.userProfile.UserProfileResponseDto;
import org.example.gongiklifeclientbegraphql.producer.user.UserLoginHistoryProducer;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserLoginHistoryProducer userLoginHistoryProducer;
  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

//  public ServiceSignUpResponseDto signUp(SignUpUserRequestDto requestDto) {
//
//    return ServiceExceptionHandlingUtil.handle("SignUpService",() -> {
//      return ServiceSignUpResponseDto.fromSignUpResponseProto(userBlockingStub.signUp(
//          requestDto.toSignUpRequestProto()));
//    });
//
//  }

  @Cacheable(value = "myProfile", key = "#userId")
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

  @Cacheable(value = "userProfile", key = "#userId")
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

  @Caching(
      evict = {
          @CacheEvict(value = "myProfile", key = "#requestDto.userId")
      },
      put = {
          @CachePut(value = "userProfile", key = "#requestDto.userId")
      }
  )
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

  public void sendUserLoginHistoryRequest(UserLoginHistoryRequestDto userLoginHistoryRequestDto) {
    userLoginHistoryProducer.sendUserLoginHistoryRequest(userLoginHistoryRequestDto);
  }

  public String hasInstitution(String userId) {
    try {
      return userBlockingStub.hasInstitution(
          HasInstitutionRequest.newBuilder().setUserId(userId).buildPartial()
      ).getInstitutionId();


    } catch (Exception e) {
      log.error("graphql -> userService -> hasInstitution error: ", e);
      throw e;
    }
  }

}
