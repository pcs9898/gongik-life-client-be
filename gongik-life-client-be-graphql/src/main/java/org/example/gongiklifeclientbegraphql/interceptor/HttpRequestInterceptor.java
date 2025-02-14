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
    HttpServletRequest servletRequest =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

    Map<String, Object> context = new HashMap<>();
    context.put("request", servletRequest);

    request.configureExecutionInput((executionInput, builder) ->
        builder.graphQLContext(context).build());

    return chain.next(request);
  }
}

