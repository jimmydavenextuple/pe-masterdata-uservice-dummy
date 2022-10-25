package com.hbc.csvdownload.service.v1;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcessingRequestFactory {

  private final Map<String, ProcessingRequestInterface> processingRequestMap;

  @Autowired
  private ProcessingRequestFactory(List<ProcessingRequestInterface> processingRequestList) {
    this.processingRequestMap =
        processingRequestList.stream()
            .collect(
                Collectors.toUnmodifiableMap(
                    ProcessingRequestInterface::getModuleType, Function.identity()));
  }

  public ProcessingRequestInterface getModule(String moduleType) {
    return Optional.ofNullable(processingRequestMap.get(moduleType))
        .orElseThrow(IllegalArgumentException::new);
  }
}
