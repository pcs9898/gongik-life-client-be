package org.example.gongiklifeclientbegraphql.interceptor;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class UserInterceptor implements WebGraphQlInterceptor {


  @Override
  public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {

    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    // WebSocket 요청일 경우 attributes가 null이므로 바로 체인을 통과시킵니다.
    if (attributes == null) {
      return chain.next(request);
    }
    log.info("GraphQL Request: {}", request.getDocument());
    log.info("Headers: {}", request.getHeaders());

    String userId = request.getHeaders().getFirst("X-USER-ID");
    log.info("User ID: {}", userId);

    request.configureExecutionInput((executionInput, executionInputBuilder) -> {

      executionInput.getGraphQLContext().put("X-USER-ID", Objects.requireNonNullElse(userId, "-1"));

      return executionInput;
    });

    return chain.next(request);
  }
}
