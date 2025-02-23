package org.example.gongiklifeclientbegraphql.service.user;

import com.gongik.userService.domain.service.UserServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.dto.user.updateProfile.UpdateProfileRequestDto;
import org.example.gongiklifeclientbegraphql.dto.user.updateProfile.UpdateProfileResponseDto;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateProfileService {

  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

  @Caching(
      evict = {
          @CacheEvict(value = "myProfile", key = "#requestDto.userId")
      },
      put = {
          @CachePut(value = "userProfile", key = "#requestDto.userId")
      }
  )
  public UpdateProfileResponseDto updateProfile(UpdateProfileRequestDto requestDto) {

    return ServiceExceptionHandlingUtil.handle("UpdateUserService", () -> {
      return UpdateProfileResponseDto.fromUpdateProfileResponseProto(userBlockingStub.updateProfile(
          requestDto.toUpdateProfileRequestProto()));
    });
  }
}
