package org.example.gongiklifeclientbeuserservice.service;

import static org.example.gongiklifeclientbeuserservice.constants.UserServiceConstants.EXPIRATION_MINUTES;
import static org.example.gongiklifeclientbeuserservice.constants.UserServiceConstants.RESEND_WAIT_MINUTES;
import static org.example.gongiklifeclientbeuserservice.constants.UserServiceConstants.VERIFICATION_CODE_PREFIX;
import static org.example.gongiklifeclientbeuserservice.constants.UserServiceConstants.VERIFIED_EMAIL_PREFIX;

import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.SendEmailVerificationCodeResponse;
import dto.mail.EmailVerificationRequestDto;
import io.grpc.Status;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeuserservice.exception.SendEmailVerificationException;
import org.example.gongiklifeclientbeuserservice.producer.EmailVerificationCodeProducer;
import org.example.gongiklifeclientbeuserservice.repository.UserRepository;
import org.example.gongiklifeclientbeuserservice.util.EmailVerificationCodeGenerator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendEmailVerificationCodeService {


  private final UserService userService;
  private final RedisTemplate<String, String> redisTemplate;
  private final EmailVerificationCodeProducer emailVerificationCodeProducer;
  private final UserRepository userRepository;

  public SendEmailVerificationCodeResponse sendEmailVerificationCode(
      SendEmailVerificationCodeRequest request) {
    String email = request.getEmail();

    validateEmailNotRegistered(email);
    validateResendTimeLimit(email);

    String verificationCode = generateAndSendVerificationCode(email);
    saveVerificationCode(email, verificationCode);

    return SendEmailVerificationCodeResponse.newBuilder()
        .setSuccess(true)
        .build();
  }

  private void validateEmailNotRegistered(String email) {
    if (userRepository.existsByEmail(email)) {
      throw Status.ALREADY_EXISTS
          .withDescription("Email is already registered.")
          .asRuntimeException();
    }
  }

  private void validateResendTimeLimit(String email) {
    String existingCode = redisTemplate.opsForValue().get(VERIFICATION_CODE_PREFIX + email);
    if (existingCode != null) {
      Long expirationTime = redisTemplate.getExpire(VERIFICATION_CODE_PREFIX + email,
          TimeUnit.MINUTES);
      if (isWithinResendWaitPeriod(expirationTime)) {
        throw Status.RESOURCE_EXHAUSTED
            .withDescription(
                String.format("Please wait for %d minutes before resending.", RESEND_WAIT_MINUTES))
            .asRuntimeException();
      }
    }
  }

  private boolean isWithinResendWaitPeriod(Long expirationTime) {
    return expirationTime != null && expirationTime >= (EXPIRATION_MINUTES - RESEND_WAIT_MINUTES);
  }

  private String generateAndSendVerificationCode(String email) {
    String verificationCode = EmailVerificationCodeGenerator.generateCode();

    EmailVerificationRequestDto requestDto = EmailVerificationRequestDto.builder()
        .email(email)
        .code(verificationCode)
        .build();

    try {
      emailVerificationCodeProducer.sendEmailVerificationRequest(requestDto);
    } catch (Exception e) {
      log.error("Failed to send email verification code", e);
      throw new SendEmailVerificationException("Failed to send verification code", e);
    }

    return verificationCode;
  }

  private void saveVerificationCode(String email, String verificationCode) {
    redisTemplate.delete(VERIFIED_EMAIL_PREFIX + email);
    userService.saveEmailVerificationCode(email, verificationCode);
  }
}
