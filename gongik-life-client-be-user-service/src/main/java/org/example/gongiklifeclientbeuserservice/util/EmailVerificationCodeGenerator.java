package org.example.gongiklifeclientbeuserservice.util;

import java.security.SecureRandom;

public class EmailVerificationCodeGenerator {

  private static final SecureRandom random = new SecureRandom();
  private static final int CODE_LENGTH = 6;

  public static String generateCode() {
    StringBuilder code = new StringBuilder(CODE_LENGTH);
    for (int i = 0; i < CODE_LENGTH; i++) {
      code.append(random.nextInt(10));
    }
    return code.toString();
  }
}