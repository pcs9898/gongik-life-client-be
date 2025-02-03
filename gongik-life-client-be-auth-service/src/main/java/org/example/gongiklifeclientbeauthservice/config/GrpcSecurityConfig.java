package org.example.gongiklifeclientbeauthservice.config;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import javax.annotation.Nullable;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Configuration
public class GrpcSecurityConfig {

  @Bean
  public GrpcAuthenticationReader grpcAuthenticationReader() {
    return new GrpcAuthenticationReader() {
      @Nullable
      @Override
      public Authentication readAuthentication(ServerCall<?, ?> call, Metadata headers)
          throws AuthenticationException {
        return null;
      }
    };
  }
}