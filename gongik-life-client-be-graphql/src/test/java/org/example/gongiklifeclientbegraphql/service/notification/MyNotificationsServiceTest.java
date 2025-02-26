package org.example.gongiklifeclientbegraphql.service.notification;

import com.gongik.notificationService.domain.service.NotificationServiceGrpc;
import com.gongik.notificationService.domain.service.NotificationServiceOuterClass;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.example.gongiklifeclientbegraphql.dto.notification.myNotifications.MyNotificationsRequestDto;
import org.example.gongiklifeclientbegraphql.dto.notification.myNotifications.MyNotificationsResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyNotificationsServiceTest {

    @Mock
    private NotificationServiceGrpc.NotificationServiceBlockingStub notificationServiceBlockingStub;

    @InjectMocks
    private MyNotificationsService myNotificationsService;

    @BeforeEach
    void setUp() {
        // @GrpcClient로 주입되는 필드를 ReflectionTestUtils를 통해 수동으로 주입합니다.
        ReflectionTestUtils.setField(myNotificationsService, "notificationServiceBlockingStub", notificationServiceBlockingStub);
    }

    @Test
    @DisplayName("성공: 알림 조회 성공")
    void myNotifications_success() {
        // Given: 테스트용 MyNotificationsRequestDto 생성
        MyNotificationsRequestDto requestDto = createTestRequestDto();
        // DTO를 Proto 객체로 변환
        NotificationServiceOuterClass.MyNotificationsRequest protoRequest = requestDto.toMyNotificationsRequestProto();

        // 더미 gRPC 응답 객체 생성 (필요한 필드를 설정)
        // Mockito의 any() 대신 더미 객체를 직접 생성하여 List.of()에 전달합니다.
        NotificationServiceOuterClass.NotificationForList dummyNotification =
                NotificationServiceOuterClass.NotificationForList.newBuilder()
                        .setTargetedNotificationId("dummy-targeted-notification-id")
                        .setContent("dummy content")
                        .setCreatedAt("222-01-01T00:00:00Z")
                        .setTargetCommentId("dummy-target-comment-id")
                        .build();

        NotificationServiceOuterClass.MyNotificationsResponse grpcResponse =
                NotificationServiceOuterClass.MyNotificationsResponse.newBuilder()
                        .addAllListNotification(List.of(dummyNotification))
                        .setPageInfo(NotificationServiceOuterClass.PageInfo.newBuilder()
                                .setEndCursor("dummy-end-cursor")
                                .setHasNextPage(true)
                                .build())
                        .build();

        // Stub이 protoRequest에 대해 grpcResponse를 반환하도록 모킹합니다.
        when(notificationServiceBlockingStub.myNotifications(eq(protoRequest))).thenReturn(grpcResponse);

        // When: 서비스 메서드 호출
        MyNotificationsResponseDto responseDto = myNotificationsService.myNotifications(requestDto);

        // Then: 반환된 응답이 null이 아니며, 필드 값이 더미 응답과 일치하는지 검증합니다.
        assertNotNull(responseDto);
        assertNotNull(responseDto.getListNotification());
        // 예시로 pageInfo의 hasNextPage 값과 endCursor 값을 검증합니다.
        assertTrue(responseDto.getPageInfo().isHasNextPage());
        assertEquals("dummy-end-cursor", responseDto.getPageInfo().getEndCursor());

        // Stub 호출 검증
        verify(notificationServiceBlockingStub).myNotifications(eq(protoRequest));
    }

    @Test
    @DisplayName("실패: gRPC 서버 에러 발생 시 알림 조회 예외 처리")
    void myNotifications_grpcError() {
        // Given: 테스트용 MyNotificationsRequestDto 생성
        MyNotificationsRequestDto requestDto = createTestRequestDto();
        // Stub 호출 시 StatusRuntimeException 발생하도록 모킹합니다.
        when(notificationServiceBlockingStub.myNotifications(any()))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL.withDescription("Internal error")));

        // When & Then: 서비스 호출 시 예외 발생 여부 검증 (메시지에 "MyNotificationsService"가 포함되어 있다고 가정)
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                myNotificationsService.myNotifications(requestDto));
        assertTrue(exception.getMessage().contains("MyNotificationsService"));
    }

    // 테스트용 MyNotificationsRequestDto 객체 생성 헬퍼 메서드
    private MyNotificationsRequestDto createTestRequestDto() {
        return MyNotificationsRequestDto.builder()
                .userId("test-user-id")
                .pageSize(10)
                // 필요하다면 추가 필드를 설정합니다.
                .build();
    }
}
