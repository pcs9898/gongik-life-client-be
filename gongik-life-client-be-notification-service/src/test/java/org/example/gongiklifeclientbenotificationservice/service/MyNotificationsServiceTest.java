package org.example.gongiklifeclientbenotificationservice.service;

import com.gongik.notificationService.domain.service.NotificationServiceOuterClass.MyNotificationsRequest;
import com.gongik.notificationService.domain.service.NotificationServiceOuterClass.MyNotificationsResponse;
import com.gongik.notificationService.domain.service.NotificationServiceOuterClass.NotificationForList;
import com.gongik.notificationService.domain.service.NotificationServiceOuterClass.PageInfo;
import org.example.gongiklifeclientbenotificationservice.entity.Notification;
import org.example.gongiklifeclientbenotificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MyNotificationsServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private MyNotificationsService myNotificationsService;

    // 헬퍼 메서드: MyNotificationsRequest 생성
    private MyNotificationsRequest buildRequest(String userId, String cursor, int pageSize, boolean hasCursor) {
        MyNotificationsRequest.Builder builder = MyNotificationsRequest.newBuilder()
                .setUserId(userId)
                .setPageSize(pageSize);
        if (hasCursor && cursor != null) {
            builder.setCursor(cursor);
        }
        return builder.build();
    }

    @Test
    @DisplayName("성공: 알림 목록 조회 및 응답 생성")
    void myNotifications_success() {
        // Arrange
        String userId = "0988961f-e359-46b2-b240-af1dd8b473dc";
        int pageSize = 2;
        // cursor 미설정(없음)
        MyNotificationsRequest request = buildRequest(userId, "", pageSize, false);

        // 동일한 시간 사용
        Date now = new Date();
        // 더미 Notification 엔티티 생성
        Notification notification1 = Notification.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .userId(UUID.fromString(userId))
                .notificationTypeId(1)
                .title("Title1")
                .content("Content1")
                .createdAt(now)
                .build();

        Notification notification2 = Notification.builder()
                .id(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                .userId(UUID.fromString(userId))
                .notificationTypeId(1)
                .title("Title2")
                .content("Content2")
                .createdAt(now)
                .build();

        List<Notification> notifications = Arrays.asList(notification1, notification2);
        when(notificationRepository.findMyNotificationsWithCursor(eq(userId), eq(null), eq(pageSize)))
                .thenReturn(notifications);

        // Act
        MyNotificationsResponse response = myNotificationsService.myNotifications(request);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getListNotificationCount());

        // 첫번째 알림 검증
        NotificationForList proto1 = response.getListNotification(0);
        assertEquals(notification1.getId().toString(), proto1.getId());
        assertEquals(notification1.getNotificationTypeId(), proto1.getNotificationTypeId());
        assertEquals(notification1.getTitle(), proto1.getTitle());
        assertEquals(notification1.getContent(), proto1.getContent());
        assertEquals(notification1.getCreatedAt().toString(), proto1.getCreatedAt());

        // 두번째 알림 검증
        NotificationForList proto2 = response.getListNotification(1);
        assertEquals(notification2.getId().toString(), proto2.getId());
        assertEquals(notification2.getNotificationTypeId(), proto2.getNotificationTypeId());
        assertEquals(notification2.getTitle(), proto2.getTitle());
        assertEquals(notification2.getContent(), proto2.getContent());
        assertEquals(notification2.getCreatedAt().toString(), proto2.getCreatedAt());

        // 페이징 검증: 목록 크기가 pageSize와 같으므로 hasNextPage는 true, endCursor는 마지막 알림의 ID
        PageInfo pageInfo = response.getPageInfo();
        assertTrue(pageInfo.getHasNextPage());
        assertEquals(notification2.getId().toString(), pageInfo.getEndCursor());
    }

    @Test
    @DisplayName("성공: 조회 결과가 없는 경우, 빈 응답 생성")
    void myNotifications_empty() {
        // Arrange
        String userId = "user-123";
        int pageSize = 10;
        MyNotificationsRequest request = buildRequest(userId, "", pageSize, false);

        when(notificationRepository.findMyNotificationsWithCursor(eq(userId), eq(""), eq(pageSize)))
                .thenReturn(Collections.emptyList());

        // Act
        MyNotificationsResponse response = myNotificationsService.myNotifications(request);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getListNotificationCount());
        // 빈 목록인 경우, hasNextPage는 false 및 endCursor는 빈 문자열(또는 기본값)이어야 함
        assertFalse(response.getPageInfo().getHasNextPage());
        assertEquals("", response.getPageInfo().getEndCursor());
    }
}
