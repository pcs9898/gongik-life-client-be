package org.example.gongiklifeclientbegraphql.service;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.HasInstitutionRequest;
import dto.user.UserLoginHistoryRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbegraphql.producer.user.UserLoginHistoryProducer;
import org.example.gongiklifeclientbegraphql.util.ServiceExceptionHandlingUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserLoginHistoryProducer userLoginHistoryProducer;
  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;


  public void sendUserLoginHistoryRequest(UserLoginHistoryRequestDto userLoginHistoryRequestDto) {
    userLoginHistoryProducer.sendUserLoginHistoryRequest(userLoginHistoryRequestDto);
  }

  public String hasInstitution(String userId) {
    return ServiceExceptionHandlingUtil.handle("UserService", () -> {
      return userBlockingStub.hasInstitution(
          HasInstitutionRequest.newBuilder().setUserId(userId).buildPartial()
      ).getInstitutionId();
    });
  }

}
