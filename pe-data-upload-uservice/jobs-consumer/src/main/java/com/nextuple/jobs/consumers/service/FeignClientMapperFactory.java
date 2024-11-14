/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
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
