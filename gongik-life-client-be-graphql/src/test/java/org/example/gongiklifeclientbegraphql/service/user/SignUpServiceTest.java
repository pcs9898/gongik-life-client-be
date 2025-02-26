package org.example.gongiklifeclientbegraphql.service.user;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass;
import com.gongik.userService.domain.service.UserServiceOuterClass.SignUpRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.user.signUp.ServiceSignUpResponseDto;
import org.example.gongiklifeclientbegraphql.dto.user.signUp.SignUpUserRequestDto;
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
class SignUpServiceTest {

    @Mock
    private UserServiceGrpc.UserServiceBlockingStub userBlockingStub;

    @InjectMocks
    private SignUpService signUpService;

    @BeforeEach
    void setUp() {
        // @GrpcClient로 주입되는 필드가 ReflectionTestUtils로 주입될 수 있도록 처리합니다.
        ReflectionTestUtils.setField(signUpService, "userBlockingStub", userBlockingStub);
    }

    @Test
    @DisplayName("회원 가입 성공")
    void signUp_Success() {
        // Given
        SignUpUserRequestDto requestDto = createTestRequestDto();
        // 요청 DTO의 proto 변환값
        SignUpRequest protoRequest = requestDto.toSignUpRequestProto();
        // Dummy gRPC 응답 생성 (필요한 필드 값을 설정)
        UserServiceOuterClass.SignUpResponse grpcResponse = UserServiceOuterClass.SignUpResponse.newBuilder()


                .build();

        when(userBlockingStub.signUp(eq(protoRequest))).thenReturn(grpcResponse);

        // When
        ServiceSignUpResponseDto responseDto = signUpService.signUp(requestDto);

        // Then
        assertNotNull(responseDto);
        // 변환 메서드의 결과를 통해 필요한 필드 검증(예: userId, email 등)을 추가할 수 있습니다.
        verify(userBlockingStub).signUp(eq(protoRequest));
    }

    @Test
    @DisplayName("회원 가입 gRPC 에러 발생 시 예외 처리")
    void signUp_WhenGrpcError() {
        // Given
        SignUpUserRequestDto requestDto = createTestRequestDto();
        when(userBlockingStub.signUp(any())).thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> signUpService.signUp(requestDto));
        assertTrue(exception.getMessage().contains("SignUpService"));
    }

    private SignUpUserRequestDto createTestRequestDto() {
        // 테스트용 SignUpUserRequestDto 객체 생성 (필요한 필드를 채워주세요)
        return SignUpUserRequestDto.builder()
                .name("test")
                .email("test@example.com")
                .password("password")
                .confirmPassword("password")
                .build();
    }
}
