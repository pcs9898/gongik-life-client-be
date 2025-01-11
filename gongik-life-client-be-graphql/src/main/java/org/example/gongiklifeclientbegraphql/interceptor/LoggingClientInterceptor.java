package org.example.gongiklifeclientbegraphql.interceptor;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import reactor.core.publisher.Mono;

@Slf4j
public class LoggingClientInterceptor implements ClientInterceptor {

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
      MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
    return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
        next.newCall(method, callOptions)) {

      @Override
      public void start(Listener<RespT> responseListener, Metadata headers) {
        log.info("gRPC 호출 시작: 메서드={}, 헤더={}", method.getFullMethodName(), headers);
        super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(
            responseListener) {
          @Override
          public void onMessage(RespT message) {
            log.info("gRPC 응답 수신: 메서드={}, 응답={}", method.getFullMethodName(), message);
            super.onMessage(message);
          }

          @Override
          public void onClose(Status status, Metadata trailers) {
            log.info("gRPC 호출 종료: 메서드={}, 상태={}, 트레일러={}", method.getFullMethodName(), status,
                trailers);
            super.onClose(status, trailers);
          }
        }, headers);
      }

      @Override
      public void sendMessage(ReqT message) {
        log.info("gRPC 요청 전송: 메서드={}, 요청={}", method.getFullMethodName(), message);
        super.sendMessage(message);
      }
    };
  }

  @Configuration
  public static class UserInterceptor implements WebGraphQlInterceptor {

    private static final Logger log = LoggerFactory.getLogger(UserInterceptor.class);

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
      log.info("GraphQL Request: {}", request.getDocument());
      log.info("Headers: {}", request.getHeaders());

      String userId = request.getHeaders().getFirst("X-USER-ID");

      request.configureExecutionInput((executionInput, executionInputBuilder) -> {
        executionInput.getGraphQLContext()
            .put("X-USER-ID", Objects.requireNonNullElse(userId, "-1"));

        return executionInput;
      });

      return chain.next(request);
    }
  }
}