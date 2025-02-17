package org.example.gongiklifeclientbeinstitutionservice.util;


import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class RandomDoubleGenerator {
  public double generateRandomDouble() {
    Random random = new Random();
    return random.nextDouble() * 5.0; // 0.0부터 5.0 사이의 랜덤 double 생성
  }
}