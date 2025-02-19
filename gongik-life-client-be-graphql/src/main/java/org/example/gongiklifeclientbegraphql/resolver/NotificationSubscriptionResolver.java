package org.example.gongiklifeclientbegraphql.resolver;

import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbegraphql.dto.notification.notificationRealTime.NotificationRealTimeResponseDto;
import org.example.gongiklifeclientbegraphql.publisher.NotificationPublisher;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@Slf4j
public class NotificationSubscriptionResolver {


  private final NotificationPublisher notificationPublisher;

  public NotificationSubscriptionResolver(NotificationPublisher notificationPublisher) {
    this.notificationPublisher = notificationPublisher;
  }

  @SubscriptionMapping
  public Flux<NotificationRealTimeResponseDto> notificationRealTime(
      DataFetchingEnvironment dataFetchingEnvironment
  ) {
    // 이미 인증 서버에서 주입된 "X-USER-ID"를 GraphQLContext에서 꺼냅니다.
    String userId = dataFetchingEnvironment.getGraphQlContext().get("X-USER-ID");

    log.info("Subscribing to notifications for user {}", userId);
    // Publisher에서 사용자 ID에 해당하는 이벤트만 필터링해서 반환
    return notificationPublisher.getNotificationsForUser(userId);
  }
}