package org.example.gongiklifeclientbegraphql.service.community;

import com.gongik.communityService.domain.service.CommunityServiceGrpc;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyCommentsRequest;
import com.gongik.communityService.domain.service.CommunityServiceOuterClass.MyCommentsResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.community.myComments.MyCommentsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.community.myComments.MyCommentsResponseDto;
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
class MyCommentsServiceTest {

    @Mock
    private CommunityServiceGrpc.CommunityServiceBlockingStub communityServiceBlockingStub;

    @InjectMocks
    private MyCommentsService myCommentsService;

    @BeforeEach
    void setUp() {
        // @GrpcClient로 주입되는 필드를 ReflectionTestUtils를 통해 주입합니다.
        ReflectionTestUtils.setField(myCommentsService, "communityServiceBlockingStub", communityServiceBlockingStub);
    }

    @Test
    @DisplayName("내 댓글 조회 성공")
    void myComments_Success() {
        // Given: 테스트용 요청 DTO 생성 및 프로토 변환
        MyCommentsRequestDto requestDto = createTestRequestDto();
        MyCommentsRequest protoRequest = requestDto.toMyCommentsRequestProto();
        // dummy gRPC 응답 객체 생성 (필요한 응답 필드를 설정)
        MyCommentsResponse grpcResponse = MyCommentsResponse.newBuilder()
                // 예를 들어, 댓글 목록이나 다른 필드를 설정할 수 있습니다.
                .build();

        when(communityServiceBlockingStub.myComments(eq(protoRequest)))
                .thenReturn(grpcResponse);

        // When: 서비스 메서드 호출
        MyCommentsResponseDto responseDto = myCommentsService.myComments(requestDto);

        // Then: 응답 DTO가 null이 아니고, 올바른 프로토 요청값으로 stub이 호출되었음을 검증
        assertNotNull(responseDto);
        verify(communityServiceBlockingStub).myComments(eq(protoRequest));
    }

    @Test
    @DisplayName("내 댓글 조회 실패: gRPC 호출 시 에러 발생")
    void myComments_WhenGrpcError() {
        // Given: 테스트용 요청 DTO 생성
        MyCommentsRequestDto requestDto = createTestRequestDto();

        when(communityServiceBlockingStub.myComments(any(MyCommentsRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL));

        // When & Then: 예외가 발생하며, 예외 메시지에 "MyCommentsService" 식별자가 포함되었는지 확인
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                myCommentsService.myComments(requestDto));
        assertTrue(exception.getMessage().contains("MyCommentsService"));
    }

    // 테스트용 MyCommentsRequestDto 객체 생성 메서드
    private MyCommentsRequestDto createTestRequestDto() {
        // 필요한 필드를 채워서 테스트용 DTO를 생성합니다.
        return MyCommentsRequestDto.builder()
                .userId("test-user-id")
                .pageSize(10)
                // 다른 필드도 DTO가 요구하는 경우 설정
                .build();
    }
}
