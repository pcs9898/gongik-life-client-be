package org.example.gongiklifeclientbeuserservice.service;

import static org.example.gongiklifeclientbeuserservice.constants.UserServiceConstants.VERIFIED_EMAIL_PREFIX;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeuserservice.constants.UserServiceConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommonUserService {


  private final RedisTemplate<String, String> redisTemplate;

  public void saveEmailVerificationCode(String email, String code) {
    String key = UserServiceConstants.VERIFICATION_CODE_PREFIX + email;
    redisTemplate.opsForValue()
        .set(key, code, UserServiceConstants.EXPIRATION_MINUTES,
            TimeUnit.MINUTES);
  }

  public boolean isEmailVerified(String email) {
    String verifiedKey = VERIFIED_EMAIL_PREFIX + email;
    String isVerified = redisTemplate.opsForValue().get(verifiedKey);

    return "true".equals(isVerified);
  }

}
