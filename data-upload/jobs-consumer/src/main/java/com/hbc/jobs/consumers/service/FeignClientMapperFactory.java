package com.hbc.jobs.consumers.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.enums.ModuleEnum;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeignClientMapperFactory {

  private final Logger logger = LoggerFactory.getLogger(FeignClientMapperFactory.class);
  private final Map<ModuleEnum, FeignClientMapper> feignClientMapperMap;

  @Autowired
  private FeignClientMapperFactory(List<FeignClientMapper> feignClientMappers) {
    this.feignClientMapperMap =
        feignClientMappers.stream()
            .collect(
                Collectors.toUnmodifiableMap(FeignClientMapper::getModule, Function.identity()));
  }

  public FeignClientMapper getFeignClientMapper(JobTypeEnum jobTypeEnum) {
    var feignClientMapper = feignClientMapperMap.get(jobTypeEnum.getModule());
    if (feignClientMapper == null) {
      logger.error("Invalid Job Type: {}", jobTypeEnum);
      throw new IllegalArgumentException("Invalid Job Type: " + jobTypeEnum);
    }
    feignClientMapper.setJobType(jobTypeEnum);
    return feignClientMapper;
  }
}
