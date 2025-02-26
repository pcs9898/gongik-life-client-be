package org.example.gongiklifeclientbecommunityservice.service;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    @GrpcClient("gongik-life-client-be-user-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

    public String getUserNameById(String userId) {
        String userName = userServiceBlockingStub.getUserNameById(
                UserServiceOuterClass.GetUserNameByIdRequest.newBuilder().setUserId(userId).build()
        ).getUserName();
        if (userName == null || userName.isEmpty()) {
            log.error("존재하지 않는 사용자 ID: {}", userId);
            throw new NotFoundException("사용자를 찾을 수 없습니다: " + userId);
        }
        return userName;
    }

    public Map<String, String> getUserNamesByIds(List<String> userIds) {
        return userServiceBlockingStub.getUserNameByIds(
                UserServiceOuterClass.GetUserNameByIdsRequest.newBuilder().addAllUserIds(userIds).build()
        ).getUsersMap();
    }
}
