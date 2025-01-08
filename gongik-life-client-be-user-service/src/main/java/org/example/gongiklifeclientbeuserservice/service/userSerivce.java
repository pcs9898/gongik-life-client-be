package org.example.gongiklifeclientbeuserservice.service;

import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeResponse;
import dto.UserToEmail.EmailVerificationRequestDto;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeuserservice.producer.EmailVerificationCodeProducer;
import org.example.gongiklifeclientbeuserservice.util.EmailVerificationCodeGenerator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class userSerivce {


  private static final long CODE_EXPIRATION_MINUTES = 6;
  private static final long RESEND_WAIT_MINUTES = 1;
  private final RedisTemplate<String, String> redisTemplate;
  private final EmailVerificationCodeProducer emailVerificationCodeProducer;

  public SendEmailVerificationCodeResponse sendEmailVerificationCode(
      SendEmailVerificationCodeRequest request) {

    String email = request.getEmail();
    String existingCode = redisTemplate.opsForValue().get("verification_code:" + email);

    if (existingCode != null) {
      Long expirationTime = redisTemplate.getExpire("verification_code:" + email, TimeUnit.MINUTES);
      log.info("existingCodeHere: " + existingCode + "expirationTime: " + expirationTime);
      if (expirationTime != null && expirationTime >= (CODE_EXPIRATION_MINUTES
          - RESEND_WAIT_MINUTES)) {
        log.info("existingCode2: " + existingCode);
        throw new RuntimeException(
            "Please wait for 1 minute before resending the code.");
      }
    }

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

  private void saveEmailVerificationCode(String email, String code) {
    String key = "verification_code:" + email;
    redisTemplate.opsForValue().set(key, code, CODE_EXPIRATION_MINUTES, TimeUnit.MINUTES);
  }


}
