package org.example.gongiklifeclientbegraphql.interceptor;

import java.time.Duration;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ResponseCookieInterceptor implements WebGraphQlInterceptor {

  @Override
  public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
    return chain.next(request).doOnNext(response -> {
      // GraphQL 컨텍스트에서 값을 가져와서 쿠키 설정
      String value = response.getExecutionInput().getGraphQLContext().get("refreshToken");
      ResponseCookie cookie = ResponseCookie.from("refreshToken", value)
          .httpOnly(true)
          .path("/")
          .maxAge(Duration.ofDays(14))
          .build();
      response.getResponseHeaders().add(HttpHeaders.SET_COOKIE, cookie.toString());
    });
  }
}
