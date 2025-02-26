package org.example.gongiklifeclientbegraphql.service.user;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.MyProfileRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.user.me.MyProfileResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyProfileService {

  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

  @Cacheable(value = "myProfile", key = "#userId")
  public MyProfileResponseDto myProfile(String userId) {

    return ServiceExceptionHandlingUtil.handle("MyProfileService", () -> {
      Assert.notNull(userId, "userId must not be null");

      return MyProfileResponseDto.fromMyProfileResponseProto(
          userBlockingStub.myProfile(
              MyProfileRequest.newBuilder()
                  .setUserId(userId)
                  .build()
          )
      );
    });
  }
}
