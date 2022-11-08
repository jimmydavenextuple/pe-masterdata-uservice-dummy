package com.nextuple.jobs.dashboard.service;

import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProcessFileContentsMapperFactory {

  private final Map<JobTypeEnum, ProcessFileContents> processFileContentsMap;

  @Autowired
  private ProcessFileContentsMapperFactory(List<ProcessFileContents> processingRequestList) {
    this.processFileContentsMap =
        processingRequestList.stream()
            .collect(
                Collectors.toUnmodifiableMap(ProcessFileContents::getJobType, Function.identity()));
  }

  public ProcessFileContents getProcessFileContentsMapper(JobTypeEnum jobType) {
    return Optional.ofNullable(processFileContentsMap.get(jobType))
        .orElseThrow(IllegalArgumentException::new);
  }
}
