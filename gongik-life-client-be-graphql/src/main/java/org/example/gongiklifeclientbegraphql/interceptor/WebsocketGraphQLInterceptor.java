package org.example.gongiklifeclientbegraphql.interceptor;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.graphql.server.WebSocketGraphQlInterceptor;
import org.springframework.graphql.server.WebSocketSessionInfo;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebsocketGraphQLInterceptor implements WebSocketGraphQlInterceptor {

  private static final ConcurrentHashMap<String, String> sessionUserMap = new ConcurrentHashMap<>();


  private final RestTemplate restTemplate;

  // connection_init 단계에서 클라이언트가 보낸 payload를 받아서 토큰을 검증하고, userId를 반환합니다.
  @Override
  public Mono<Object> handleConnectionInitialization(WebSocketSessionInfo sessionInfo,
      Map<String, Object> connectionInitPayload) {
    log.info("connectionInitPayload: {}", connectionInitPayload);

    String authHeader = connectionInitPayload != null
        ? (String) connectionInitPayload.get("Authorization")
        : null;

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return Mono.error(new RuntimeException("Missing or invalid Authorization token"));
    }
    String accessToken = authHeader.substring(7);

    // token 검증을 다른 MSA(예, 인증 서비스)에 요청하여 검증 후 userId를 받아옵니다.
    // 실제 서비스 URL로 변경하십시오.
    String userId = sendValidateTokenPostRequest(accessToken);

    String secWebsocketKey = sessionInfo.getHeaders()
        .getFirst("sec-websocket-key");

    log.info("User {} connected with session {}", sessionInfo.getHeaders().get("sec-websocket-key"),
        userId);
    sessionUserMap.put(secWebsocketKey, userId);

    return Mono.just(Collections.singletonMap("message", "Connection initialized"));
  }

  // 실제 GraphQL 요청이 처리되기 전에 GraphQLContext에 userId(혹은 다른 정보를) 추가합니다.
  @Override
  public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      return chain.next(request);
    }

    // 후속 GraphQL 요청에서 세션 ID로 사용자 식별 가능
    String secWebsocketKey = request.getHeaders()
        .getFirst("sec-websocket-key"); // WebSocket 세션 키를 사용하여 secWebsocketKey를 가져옵니다.
    String userId = sessionUserMap.get(secWebsocketKey);

    sessionUserMap.remove(secWebsocketKey);

//
    log.info("GraphQL 요청 헤더의 sec-websocket-key: {}", secWebsocketKey);
    log.info("조회된 userId: {}", userId);
//
    log.info("User {} is making a request", userId);
    request.configureExecutionInput((executionInput, executionInputBuilder) -> {

      executionInput.getGraphQLContext().put("X-USER-ID", Objects.requireNonNullElse(userId, "-1"));
      executionInput.getGraphQLContext().put("X-USER-ID", Objects.requireNonNullElse(userId, "-1"));

      return executionInput;
    });

    return chain.next(request);
  }

  // restTemplate을 사용하여 토큰 검증 API를 호출한 후 userId를 반환합니다.
  public String sendValidateTokenPostRequest(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.set("Content-Type", "application/json");

    String url = "http://gongik-life-client-be-auth-service/api/auth/validateAccessToken";

    HttpEntity<Object> entity = new HttpEntity<>("", headers);

    ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
      // 응답 구조에 따라 적절하게 userId를 추출합니다.
      Map<String, Object> resultMap = (Map<String, Object>) response.getBody().get("result");
      return resultMap.get("userId").toString();
    }
    throw new RuntimeException("Token validation failed");
  }

  // GraphQLRequest에 포함된 헤더나 자체 저장한 값에서 accessToken을 추출하는 메서드.
  // 실제 구현은 환경에 따라 달라집니다.
  private String extractAccessToken(WebGraphQlRequest request) {
    // 예) HTTP 헤더가 포함되어 있다면:
    String token = request.getHeaders().getFirst("Authorization");
    if (token != null && token.startsWith("Bearer ")) {
      return token.substring(7);
    }
    throw new RuntimeException("Access token is missing in the WebSocket request");
  }
}