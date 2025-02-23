package org.example.gongiklifeclientbegraphql.service.user;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.UserProfileRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.user.userProfile.UserProfileResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileService {

  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

  @Cacheable(value = "userProfile", key = "#userId")
  public UserProfileResponseDto userProfile(String userId) {
    Assert.notNull(userId, "userId must not be null");

    return ServiceExceptionHandlingUtil.handle("UserProfileService", () -> {
      return UserProfileResponseDto.fromProto(
          userBlockingStub.userProfile(
              UserProfileRequest.newBuilder()
                  .setUserId(userId)
                  .build()
          ));
    });
  }
}
