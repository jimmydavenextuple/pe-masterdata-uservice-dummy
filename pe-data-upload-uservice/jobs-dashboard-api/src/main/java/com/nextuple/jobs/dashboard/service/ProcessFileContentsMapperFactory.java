/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

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
