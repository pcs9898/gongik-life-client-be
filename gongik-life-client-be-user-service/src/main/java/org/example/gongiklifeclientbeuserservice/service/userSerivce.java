package org.example.gongiklifeclientbeuserservice.service;

import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeResponse;
import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.VerifyEmailCodeResponse;
import dto.UserToEmail.EmailVerificationRequestDto;
import io.grpc.Status;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeuserservice.producer.EmailVerificationCodeProducer;
import org.example.gongiklifeclientbeuserservice.repository.UserRepository;
import org.example.gongiklifeclientbeuserservice.util.EmailVerificationCodeGenerator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class userSerivce {


  private static final long EMAIL_VERIFICATION_CODE_EXPIRATION_MINUTES = 6;
  private static final long EMAIL_VERIFICATION_CODE_RESEND_WAIT_MINUTES = 1;
  private static final long VERIFIED_EMAIL_EXPIRATION_MINUTES = 30;
  private static final String VERIFIED_EMAIL = "verified:email:";
  private static final String VERIFICATION_CODE = "verification:code:";


  private final RedisTemplate<String, String> redisTemplate;
  private final EmailVerificationCodeProducer emailVerificationCodeProducer;
  private final UserRepository userRepository;

  public SendEmailVerificationCodeResponse sendEmailVerificationCode(
      SendEmailVerificationCodeRequest request) {

    boolean emailExists = userRepository.existsByEmail(request.getEmail());
    if (emailExists) {
      throw Status.ALREADY_EXISTS
          .withDescription("Email is already registered.")
          .asRuntimeException();
    }

    String email = request.getEmail();
    String existingCode = redisTemplate.opsForValue().get(VERIFICATION_CODE + email);

    if (existingCode != null) {
      Long expirationTime = redisTemplate.getExpire(VERIFICATION_CODE + email, TimeUnit.MINUTES);

      if (expirationTime != null && expirationTime >= (EMAIL_VERIFICATION_CODE_EXPIRATION_MINUTES
          - EMAIL_VERIFICATION_CODE_RESEND_WAIT_MINUTES)) {

        throw Status.RESOURCE_EXHAUSTED
            .withDescription("Please wait for " + EMAIL_VERIFICATION_CODE_RESEND_WAIT_MINUTES
                + " minutes before resending.")
            .asRuntimeException();
      }
    }

    redisTemplate.delete(VERIFIED_EMAIL + email);

    String VerificationCode = EmailVerificationCodeGenerator.generateCode();

    EmailVerificationRequestDto emailVerificationRequestDto = EmailVerificationRequestDto.builder()
        .email(request.getEmail())
        .code(VerificationCode)
        .build();

    CompletableFuture<Boolean> responseFuture = emailVerificationCodeProducer.sendEmailVerificationRequest(
        emailVerificationRequestDto);

    boolean response;

    try {
      responseFuture.get();

      response = true;
    } catch (Exception e) {
      log.error("Error occurred while sending email verification request: ", e);

      throw new RuntimeException("Error occurred while sending email verification request: ", e);
    }
//
    saveEmailVerificationCode(request.getEmail(), VerificationCode);

    return SendEmailVerificationCodeResponse.newBuilder().setSuccess(response).build();
  }

  public VerifyEmailCodeResponse verifyEmailCode(VerifyEmailCodeRequest request) {
    String email = request.getEmail();
    String code = request.getCode();
    String key = VERIFICATION_CODE + email;

    // 이미 인증된 이메일인지 확인
    String verifiedKey = VERIFIED_EMAIL + email;
    String isVerified = redisTemplate.opsForValue().get(verifiedKey);

    if (isVerified != null) {
      throw Status.ALREADY_EXISTS
          .withDescription("Email is already verified.")
          .asRuntimeException();
    }

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

    // 인증 코드가 유효하면 Redis에서 해당 데이터 삭제
    redisTemplate.delete(key);

    // 인증 완료 데이터를 Redis에 시간 제한을 두고 저장 (예: 24시간)
    redisTemplate.opsForValue()
        .set(verifiedKey, "true", VERIFIED_EMAIL_EXPIRATION_MINUTES, TimeUnit.MINUTES);

    return VerifyEmailCodeResponse.newBuilder().setSuccess(true).build();
  }

  private void saveEmailVerificationCode(String email, String code) {
    String key = VERIFICATION_CODE + email;
    redisTemplate.opsForValue()
        .set(key, code, EMAIL_VERIFICATION_CODE_EXPIRATION_MINUTES, TimeUnit.MINUTES);
  }


}
