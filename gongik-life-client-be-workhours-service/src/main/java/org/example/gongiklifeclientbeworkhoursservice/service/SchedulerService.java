package org.example.gongiklifeclientbeworkhoursservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerService {

  private final JobLauncher jobLauncher;
  private final Job workhoursStatisticsJob;
  

  // 매주 월요일 0시(서버 타임존 기준)에 실행
  @Scheduled(cron = "0 0 0 * * MON")
  public void runWorkhoursStatisticsJob() throws Exception {
    JobParameters params = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();
    jobLauncher.run(workhoursStatisticsJob, params);
  }
}
