package org.example.gongiklifeclientbegraphql.publisher;

import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.notification.notificationRealTime.NotificationRealTimeResponseDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@Slf4j
public class NotificationPublisher {


  // 멀티캐스트 방식의 Sinks; 필요에 따라 backpressure 정책을 조정
  private final Sinks.Many<NotificationRealTimeResponseDto> sink = Sinks.many().multicast()
      .directBestEffort();

  // 외부(Kafka Consumer)에서 호출하여 알림을 발행
  public void publish(NotificationRealTimeResponseDto notification) {
    Sinks.EmitResult result = sink.tryEmitNext(notification);
    log.info("Sink emit result: {}", result);
  }

  // 사용자 ID별로 필터링해서 구독자에게 반환 (매개변수는 String userId)
  public Flux<NotificationRealTimeResponseDto> getNotificationsForUser(String userId) {
    return sink.asFlux()
        .filter(notification -> {
          boolean match = notification.getUserId().equals(userId);
          log.info("Filtering notification for user {}: {}", userId, match);
          return match;
        });
  }
}