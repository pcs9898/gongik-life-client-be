package org.example.gongiklifeclientbeworkhoursservice.batch.trigger;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupJobTrigger {

  private final JobLauncher jobLauncher;
  private final Job workhoursStatisticsJob;

  @EventListener(ApplicationReadyEvent.class)
  public void runJobOnStartup() throws Exception {
    JobParameters params = new JobParametersBuilder()
        .addLong("startupTime", System.currentTimeMillis())
        .toJobParameters();
    jobLauncher.run(workhoursStatisticsJob, params);
  }
}