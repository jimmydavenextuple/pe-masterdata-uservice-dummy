/*
 *
 *  * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *  *
 *  * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 *  * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 *
 */

package com.nextuple.jobs.consumers.domain.mapper;

import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponseForNotification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JobsFpmMapper {

  @Mapping(target = "metadata", ignore = true)
  JobResponseForNotification toJobResponseForNotification(JobResponse jobResponse);

  @Mapping(
      target = "status",
      source = "status",
      qualifiedByName = "com.nextuple.plt.domain.enums.JobStatusEnum")
  @Mapping(target = "metadata", ignore = true)
  JobResponseForNotification toJobResponseForNotification(
      com.nextuple.plt.domain.outbound.JobResponse jobResponse);

  @Named("com.nextuple.plt.domain.enums.JobStatusEnum")
  default JobStatusEnum mapJobStatusEnum(com.nextuple.plt.domain.enums.JobStatusEnum status) {
    return status != null ? JobStatusEnum.valueOf(status.name()) : null;
  }
}
