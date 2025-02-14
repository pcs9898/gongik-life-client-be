package org.example.gongiklifeclientbeauthservice.grpc;

import com.gongik.authservice.domain.service.AuthServiceGrpc;
import com.gongik.authservice.domain.service.AuthServiceOuterClass.GenerateTokenRequest;
import com.gongik.authservice.domain.service.AuthServiceOuterClass.GenerateTokenResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.gongiklifeclientbeauthservice.dto.TokenDto;
import org.example.gongiklifeclientbeauthservice.service.AuthService;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class AuthGrpcService extends AuthServiceGrpc.AuthServiceImplBase {

  private final AuthService authService;

  @Override
  public void generateToken(GenerateTokenRequest request,
      StreamObserver<GenerateTokenResponse> responseObserver) {
    try {
      TokenDto tokenDto = authService.generateTokenAndSaveRefreshToken(request.getUserId());
      GenerateTokenResponse response = GenerateTokenResponse.newBuilder()
          .setAccessToken(tokenDto.getAccessToken())
          .setRefreshToken(tokenDto.getRefreshToken())
          .setAccessTokenExpiresAt(tokenDto.getAccessTokenExpiresAt())
          .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("generateToken error : ", e);

      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)  // 원인 예외 포함
              .asRuntimeException()
      );
    }
  }
}
