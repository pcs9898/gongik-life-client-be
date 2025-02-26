package org.example.gongiklifeclientbegraphql.service.user;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.HasInstitutionRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.HasInstitutionResponse;
import dto.user.UserLoginHistoryRequestDto;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.producer.user.UserLoginHistoryProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {


    @Mock
    private UserLoginHistoryProducer userLoginHistoryProducer;

    @Mock
    private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // @GrpcClient로 주입되는 필드의 경우 ReflectionTestUtils를 사용하여 주입합니다.
        ReflectionTestUtils.setField(userService, "userBlockingStub", userBlockingStub);
    }

    @Test
    @DisplayName("사용자 로그인 이력 전송 요청 성공")
    void sendUserLoginHistoryRequest_Success() {
        // Given
        UserLoginHistoryRequestDto requestDto = new UserLoginHistoryRequestDto();
        // (필요 시 requestDto 필드에 값을 세팅)

        // When
        userService.sendUserLoginHistoryRequest(requestDto);

        // Then : producer의 sendUserLoginHistoryRequest()가 한번 호출되었는지 검증
        verify(userLoginHistoryProducer).sendUserLoginHistoryRequest(eq(requestDto));
    }

    @Test
    @DisplayName("hasInstitution 성공 케이스")
    void hasInstitution_Success() {
        // Given
        String testUserId = "test-user-id";
        String expectedInstitutionId = "expected-inst-id";
        HasInstitutionRequest expectedRequest = HasInstitutionRequest.newBuilder()
                .setUserId(testUserId)
                .buildPartial();
        HasInstitutionResponse grpcResponse = HasInstitutionResponse.newBuilder()
                .setInstitutionId(expectedInstitutionId)
                .build();

        when(userBlockingStub.hasInstitution(eq(expectedRequest))).thenReturn(grpcResponse);

        // When
        String actualInstitutionId = userService.hasInstitution(testUserId);

        // Then
        assertNotNull(actualInstitutionId);
        assertEquals(expectedInstitutionId, actualInstitutionId);
        verify(userBlockingStub).hasInstitution(eq(expectedRequest));
    }

    @Test
    @DisplayName("hasInstitution gRPC 에러 발생 시 예외 처리")
    void hasInstitution_WhenGrpcError() {
        // Given
        String testUserId = "test-user-id";
        when(userBlockingStub.hasInstitution(any(HasInstitutionRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.hasInstitution(testUserId)
        );
        assertTrue(exception.getMessage().contains("HasInstitutionUserService"));
    }
}