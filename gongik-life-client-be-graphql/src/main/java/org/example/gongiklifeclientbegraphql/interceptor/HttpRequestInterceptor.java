package org.example.gongiklifeclientbegraphql.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Mono;

@Component
public class HttpRequestInterceptor implements WebGraphQlInterceptor {

  @Override
  public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
    // RequestContextHolder를 통해 현재 요청의 속성을 가져옵니다.
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    // WebSocket 요청일 경우 attributes가 null이므로 바로 체인을 통과시킵니다.
    if (attributes == null) {
      return chain.next(request);
    }

    // HTTP 요청일 경우 HttpServletRequest를 가져와 GraphQL Context에 추가합니다.
    HttpServletRequest servletRequest = attributes.getRequest();
    Map<String, Object> context = new HashMap<>();
    context.put("request", servletRequest);

    request.configureExecutionInput((executionInput, builder) ->
        builder.graphQLContext(context).build());

    return chain.next(request);
  }
}