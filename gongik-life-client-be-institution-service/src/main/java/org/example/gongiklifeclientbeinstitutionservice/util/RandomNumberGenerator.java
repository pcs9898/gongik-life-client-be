package org.example.gongiklifeclientbeinstitutionservice.util;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class RandomNumberGenerator {

  public int generateRandomNumber() {
    Random random = new Random();
    return random.nextInt(480) + 1; // 1부터 480 사이의 랜덤 정수 생성
  }
}