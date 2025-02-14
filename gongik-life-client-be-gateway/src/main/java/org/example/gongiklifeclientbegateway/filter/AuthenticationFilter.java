package org.example.gongiklifeclientbegateway.filter;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthenticationFilter extends
    AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

  @LoadBalanced
  private final WebClient webClient;

  public AuthenticationFilter(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
    super(Config.class);
    this.webClient = WebClient.builder()
        .filter(lbFunction)
        .baseUrl("http://gongik-life-client-be-auth-service")
        .build();
  }


  @Override
  public GatewayFilter apply(Config config) {

    return (exchange, chain) -> {
      String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        String token = authHeader.substring(7);

        return validateToken(token)
            .flatMap(userId ->
                proceedWithUserId(userId, exchange, chain))
            .switchIfEmpty(
                chain.filter(exchange)) // If token is invalid, continue without setting userId
            .onErrorResume(e -> handleAuthenticationError(exchange, e)); // Handle errors
      }

      return chain.filter(exchange);
    };
  }

  private Mono<Void> handleAuthenticationError(ServerWebExchange exchange, Throwable e) {
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    return exchange.getResponse().setComplete();
  }

  private Mono<String> validateToken(String token) {
    return webClient.post()
        .uri("/api/auth/validateAccessToken")
        .header("Authorization", "Bearer " + token)
        .header("Content-Type", "application/json")
        .exchangeToMono(response -> {
          if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(Map.class)
                .map(body -> {

                  return ((Map<String, Object>) body.get("result")).get("userId").toString();
                });
          } else {

            return Mono.empty();
          }
        });
  }

  private Mono<Void> proceedWithUserId(String userId, ServerWebExchange exchange,
      GatewayFilterChain chain) {

    log.info("userId: {}", userId);
    if (userId != null) {
      ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
          .header("X-USER-ID", userId)
          .build();
      exchange = exchange.mutate().request(mutatedRequest).build();
    }
    return chain.filter(exchange);
  }

  public static class Config {
    // 필터 구성을 위한 설정 클래스
  }
}
