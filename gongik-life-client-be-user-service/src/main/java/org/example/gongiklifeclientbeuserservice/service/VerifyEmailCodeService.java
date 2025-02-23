package org.example.gongiklifeclientbeuserservice.service;

import static org.example.gongiklifeclientbeuserservice.constants.UserServiceConstants.EXPIRATION_MINUTES;
import static org.example.gongiklifeclientbeuserservice.constants.UserServiceConstants.VERIFICATION_CODE_PREFIX;
import static org.example.gongiklifeclientbeuserservice.constants.UserServiceConstants.VERIFIED_EMAIL_PREFIX;

import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeResponse;
import io.grpc.Status;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerifyEmailCodeService {

  private final RedisTemplate<String, String> redisTemplate;
  private final UserService userService;

  public VerifyEmailCodeResponse verifyEmailCode(VerifyEmailCodeRequest request) {
    String email = request.getEmail();
    String code = request.getCode();

    validateNotAlreadyVerified(email);
    validateVerificationCode(email, code);

    processSuccessfulVerification(email);

    return VerifyEmailCodeResponse.newBuilder()
        .setSuccess(true)
        .build();
  }


  private void validateNotAlreadyVerified(String email) {
    if (userService.isEmailVerified(email)) {
      throw Status.ALREADY_EXISTS
          .withDescription("Email is already verified.")
          .asRuntimeException();
    }
  }


  private void validateVerificationCode(String email, String code) {
    String key = VERIFICATION_CODE_PREFIX + email;
    String storedCode = redisTemplate.opsForValue().get(key);

    if (storedCode == null) {
      throw Status.NOT_FOUND
          .withDescription("Expired verification code.")
          .asRuntimeException();
    }

    if (!storedCode.equals(code)) {
      throw Status.INVALID_ARGUMENT
          .withDescription("Invalid verification code.")
          .asRuntimeException();
    }
  }


  private void processSuccessfulVerification(String email) {
    redisTemplate.delete(VERIFICATION_CODE_PREFIX + email);

    saveVerificationStatus(email);

    log.info("Email verification successful for: {}", email);
  }


  private void saveVerificationStatus(String email) {
    redisTemplate.opsForValue().set(
        VERIFIED_EMAIL_PREFIX + email,
        "true",
        EXPIRATION_MINUTES,
        TimeUnit.MINUTES
    );
  }
}
