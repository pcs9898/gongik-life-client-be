package org.example.gongiklifeclientbegraphql.config;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import reactor.core.publisher.Mono;

@Configuration
public class UserInterceptor implements WebGraphQlInterceptor {

  private static final Logger log = LoggerFactory.getLogger(UserInterceptor.class);

  @Override
  public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
    log.info("GraphQL Request: {}", request.getDocument());
    log.info("Headers: {}", request.getHeaders());

    String userId = request.getHeaders().getFirst("X-USER-ID");

    request.configureExecutionInput((executionInput, executionInputBuilder) -> {
      executionInput.getGraphQLContext().put("X-USER-ID", Objects.requireNonNullElse(userId, "-1"));

      return executionInput;
    });

    return chain.next(request);
  }
}
