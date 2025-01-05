package org.example.gongiklifeclientbeinstitutionservice.batch.config;

import lombok.RequiredArgsConstructor;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionRepository;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataCheckDecider implements JobExecutionDecider {

  private final InstitutionRepository institutionRepository;

  @Override
  public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
    if (institutionRepository.existsAny()) {
      return new FlowExecutionStatus("DATA_EXISTS");
    } else {
      return new FlowExecutionStatus("NO_DATA");
    }
  }
}