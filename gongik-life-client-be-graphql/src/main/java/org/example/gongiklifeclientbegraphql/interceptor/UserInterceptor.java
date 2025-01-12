package org.example.gongiklifeclientbegraphql.interceptor;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class UserInterceptor implements WebGraphQlInterceptor {


  @Override
  public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
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
