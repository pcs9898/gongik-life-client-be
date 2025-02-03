package org.example.gongiklifeclientbeinstitutionservice.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionRepository;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataCheckDecider implements JobExecutionDecider {

  private final InstitutionRepository institutionRepository;

  @Override
  public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
    if (institutionRepository.existsAny()) {
      log.info("Data exists in the database");
      return new FlowExecutionStatus("DATA_EXISTS");
    } else {
      log.info("No data in the database");
      return new FlowExecutionStatus("NO_DATA");
    }
  }
}