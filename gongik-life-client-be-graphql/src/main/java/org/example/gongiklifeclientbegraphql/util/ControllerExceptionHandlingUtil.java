package org.example.gongiklifeclientbegraphql.util;

import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ControllerExceptionHandlingUtil {

  private ControllerExceptionHandlingUtil() {
    throw new AssertionError("유틸리티 클래스로 인스턴스화 할 수 없습니다.");
  }

  public static <T> T handle(Supplier<T> action) {
    try {
      return action.get();
    } catch (Exception e) {
      throw e;
    }
  }

  // 폴백값을 반환하는 예시: 호출 실패 시 기본값을 반환
  public static void handle(Runnable action) {
    try {
      action.run();
    } catch (Exception e) {
      throw e;
    }
  }

  // 반환값이 없는 작업(Runnable)을 위한 오버로드 메서드
  public static <T> T handleWithFallback(String controllerName, Supplier<T> action, T fallback) {
    try {
      return action.get();
    } catch (Exception e) {
      log.error("{} error occurred: {} - Returning fallback", controllerName, e.getMessage(), e);
      return fallback;
    }
  }
}
