package org.example.gongiklifeclientbenotificationservice.service;

import dto.notification.CreateNotificationRequestDto;
import dto.notification.SendNotificationRequestDto;
import org.example.gongiklifeclientbenotificationservice.entity.Notification;
import org.example.gongiklifeclientbenotificationservice.producer.SendNotificationProducer;
import org.example.gongiklifeclientbenotificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateNotificationServiceTest {


    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SendNotificationProducer sendNotificationProducer;

    @InjectMocks
    private CreateNotificationService createNotificationService;

    private CreateNotificationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        // 정상적인 UUID 문자열을 사용하여 테스트 DTO 생성
        requestDto = CreateNotificationRequestDto.builder()
                .userId("11111111-1111-1111-1111-111111111111")
                .notificationTypeId(1)
                .title("Test Notification")
                .content("This is a test notification")
                .postId("22222222-2222-2222-2222-222222222222")
                .targetCommentId("33333333-3333-3333-3333-333333333333")
                .build();
    }

    @Test
    @DisplayName("성공: 알림 생성 후 전송 요청")
    void createNotification_success() {
        // Arrange
        // repository.save() 호출 시 저장된 알림 엔티티를 반환하도록 목 설정
        UUID generatedId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        Notification dummyNotification = Notification.builder()
                .id(generatedId)
                .userId(UUID.fromString(requestDto.getUserId()))
                .notificationTypeId(requestDto.getNotificationTypeId())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .postId(UUID.fromString(requestDto.getPostId()))
                .targetCommentId(UUID.fromString(requestDto.getTargetCommentId()))
                .createdAt(new Date())
                .build();
        when(notificationRepository.save(any(Notification.class))).thenReturn(dummyNotification);

        // Act
        createNotificationService.createNotification(requestDto);

        // Assert
        // repository.save() 호출 검증 및 전달된 Notification 객체의 필드 값 검증
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());
        Notification capturedNotification = notificationCaptor.getValue();
        assertEquals(UUID.fromString(requestDto.getUserId()), capturedNotification.getUserId());
        assertEquals(requestDto.getNotificationTypeId(), capturedNotification.getNotificationTypeId());
        assertEquals(requestDto.getTitle(), capturedNotification.getTitle());
        assertEquals(requestDto.getContent(), capturedNotification.getContent());
        assertEquals(UUID.fromString(requestDto.getPostId()), capturedNotification.getPostId());
        assertEquals(UUID.fromString(requestDto.getTargetCommentId()), capturedNotification.getTargetCommentId());

        // producer.sendNotificationRequest() 호출 검증
        ArgumentCaptor<SendNotificationRequestDto> producerCaptor = ArgumentCaptor.forClass(SendNotificationRequestDto.class);
        verify(sendNotificationProducer).sendNotificationRequest(producerCaptor.capture());
        SendNotificationRequestDto capturedProducerDto = producerCaptor.getValue();
        assertNotNull(capturedProducerDto);
        // 저장된 notification의 id가 producer에 전달된 DTO에 반영되었는지 확인
        assertEquals(generatedId.toString(), capturedProducerDto.getId());
        assertEquals(requestDto.getTitle(), capturedProducerDto.getTitle());
        assertEquals(requestDto.getContent(), capturedProducerDto.getContent());
    }

    @Test
    @DisplayName("실패: 잘못된 UUID 형식으로 인해 예외 발생")
    void createNotification_invalidUuid() {
        // Arrange : userId에 잘못된 UUID 문자열 설정
        CreateNotificationRequestDto invalidDto = CreateNotificationRequestDto.builder()
                .userId("invalid-uuid")
                .notificationTypeId(1)
                .title("Test Notification")
                .content("This is a test notification")
                .build();

        // Act & Assert : 잘못된 UUID 형식으로 인해 IllegalArgumentException 발생 확인
        assertThrows(IllegalArgumentException.class, () ->
                createNotificationService.createNotification(invalidDto)
        );
        // repository나 producer는 호출되지 않아야 함.
        verify(notificationRepository, never()).save(any());
        verify(sendNotificationProducer, never()).sendNotificationRequest(any());
    }
}