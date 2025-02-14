package org.example.gongiklifeclientbeinstitutionservice.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobDurationListener implements JobExecutionListener {

  private long startTime;

  @Override
  public void beforeJob(JobExecution jobExecution) {
    startTime = System.currentTimeMillis();
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    log.info("총 소요 시간: " + duration + " 밀리초"); // 10분 50초 소요
  }
}