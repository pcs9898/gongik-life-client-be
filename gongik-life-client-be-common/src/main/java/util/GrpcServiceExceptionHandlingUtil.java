package util;


import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

/**
 * 여러 gRPC 서비스 메서드에서 중복으로 사용되는 try–catch 로직을
 * <p>
 * 공통적으로 처리하기 위한 유틸리티 클래스입니다.
 */
@Slf4j
public final class GrpcServiceExceptionHandlingUtil {

  // 인스턴스화를 막기 위한 private 생성자
  private GrpcServiceExceptionHandlingUtil() {
    throw new AssertionError("GrpcUtil 클래스는 인스턴스화 할 수 없습니다.");
  }

  /**
   * 반환값이 필요한 작업을 실행하고, 예외 발생 시 로깅 후 gRPC 오류 응답을 전송합니다.
   *
   * @param methodName       처리 중인 메서드의 이름 또는 설명
   * @param action           실행할 작업 (반환값이 있는 Supplier)
   * @param responseObserver gRPC 응답 전송 객체
   * @param <T>              작업 결과의 타입
   */
  public static <T> void handle(String methodName, Supplier<T> action,
      StreamObserver<T> responseObserver) {
    try {
      T response = action.get();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.info("{} error: {}", methodName, e.getMessage(), e);
      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)
              .asRuntimeException()
      );
    }
  }

  /**
   * 반환값이 없는 작업(Runnable)을 실행하고, 예외 발생 시 로깅 후 gRPC 오류 응답을 전송합니다.
   *
   * @param methodName       처리 중인 메서드의 이름 또는 설명
   * @param action           실행할 작업 (반환값이 없는 Runnable)
   * @param responseObserver gRPC 응답 전송 객체
   */
  public static void handleStreaming(String methodName, Runnable action,
      StreamObserver<?> responseObserver) {
    try {
      action.run();
    } catch (Exception e) {
      log.info("{} error: {}", methodName, e.getMessage(), e);
      responseObserver.onError(
          Status.INTERNAL
              .withDescription(e.getMessage())
              .withCause(e)
              .asRuntimeException()
      );
    }
  }
}