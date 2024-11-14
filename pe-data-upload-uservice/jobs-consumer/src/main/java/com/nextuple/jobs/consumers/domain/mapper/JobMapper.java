/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.domain.mapper;

import com.nextuple.jobs.consumers.domain.entity.JobEntity;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JobMapper {

  JobMapper INSTANCE = Mappers.getMapper(JobMapper.class);

  JobDto toJob(JobEntity jobEntity);

  JobResponse toJobResponse(JobEntity jobEntity);

  JobEntity toJobEntity(JobDto jobDto);

  List<JobDto> toJobList(List<JobEntity> jobEntities);

  List<JobResponse> toJobResponseList(List<JobEntity> jobEntities);

  List<JobEntity> toJobEntityList(List<JobDto> jobDtos);

  JobEntity updateJobEntity(JobDto jobDto, @MappingTarget JobEntity oldJobEntity);

  JobEntity updateJobEntity(JobResponse jobResponse, @MappingTarget JobEntity oldJobEntity);
}
