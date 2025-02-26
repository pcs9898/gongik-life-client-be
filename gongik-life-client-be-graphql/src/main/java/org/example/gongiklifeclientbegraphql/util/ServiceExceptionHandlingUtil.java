package org.example.gongiklifeclientbegraphql.util;

import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ServiceExceptionHandlingUtil {

  // 인스턴스화를 막기 위한 private 생성자
  private ServiceExceptionHandlingUtil() {
    throw new AssertionError("유틸리티 클래스는 인스턴스화 할 수 없습니다.");
  }

  /**
   * 반환값이 있는 작업을 실행하며 예외 발생 시 로깅 후 예외를 재던집니다.
   *
   * @param <T>         작업 결과 타입
   * @param serviceName 해당 작업의 이름(또는 설명)
   * @param action      실행할 작업 (람다)
   * @return 작업 실행 결과
   * @throws Exception 발생한 예외를 그대로 재던짐
   */
  public static <T> T handle(String serviceName, Supplier<T> action) {
    try {
      return action.get();
    } catch (Exception e) {
      log.error("Error occurred in {} : {}", serviceName, e.getMessage(), e);
      throw new RuntimeException("Error occurred in " + serviceName + " : " + e.getMessage(), e);
    }
  }

  /**
   * 반환값이 없는 작업(Runnable)을 실행하며 예외 발생 시 로깅 후 예외를 래핑하여 던집니다.
   *
   * @param serviceName 해당 작업의 이름(또는 설명)
   * @param action      실행할 작업 (람다)
   */
  public static void handle(String serviceName, Runnable action) {
    try {
      action.run();
    } catch (Exception e) {
      log.error("Error occurred in {} : {}", serviceName, e.getMessage(), e);
      throw new RuntimeException("Error occurred in " + serviceName + " : " + e.getMessage(), e);
    }
  }

  /**
   * 반환값이 있는 작업을 실행하며, 예외 발생 시 로깅 후 지정된 폴백값을 반환합니다.
   *
   * @param <T>         작업 결과 타입
   * @param serviceName 해당 작업의 이름(또는 설명)
   * @param action      실행할 작업 (람다)
   * @param fallback    예외 발생 시 반환할 기본값
   * @return 작업 실행 결과 혹은 폴백값
   */
  public static <T> T handleWithFallback(String serviceName, Supplier<T> action, T fallback) {
    try {
      return action.get();
    } catch (Exception e) {
      log.error("{} error occurred: {} - Returning fallback", serviceName, e.getMessage(), e);
      return fallback;
    }
  }
}