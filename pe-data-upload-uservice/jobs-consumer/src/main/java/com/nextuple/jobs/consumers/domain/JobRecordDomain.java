/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.domain;

import com.nextuple.jobs.consumers.domain.entity.JobRecordEntity;
import com.nextuple.jobs.consumers.domain.mapper.JobRecordMapper;
import com.nextuple.jobs.consumers.domain.repository.JobRecordRepository;
import com.nextuple.jobs.consumers.exception.JobRecordDomainException;
import com.nextuple.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JobRecordDomain {

  private final JobRecordRepository jobRecordRepository;

  public JobRecordDomain(JobRecordRepository jobRecordRepository) {
    this.jobRecordRepository = jobRecordRepository;
  }

  /**
   * Create job resource
   *
   * @param jobRecordEntity Entity to be created
   * @return Created Job ID
   */
  public JobRecordEntity create(JobRecordEntity jobRecordEntity) throws JobRecordDomainException {
    try {
      return jobRecordRepository.save(jobRecordEntity);
    } catch (Exception e) {
      log.error("Error while persisting job record entity.", e);
      throw new JobRecordDomainException(
          "Exception while persisting the job record", e, jobRecordEntity.getId());
    }
  }

  public List<RecordStatusDto> findConsumerJobsByJobParam(
      String orgId, String jobId, Optional<String> status) {

    return JobRecordMapper.INSTANCE.toRecordStatusDtoList(
        jobRecordRepository.findJobRecordsByFilters(orgId, jobId, status.orElse(null)));
  }

  public Page<RecordStatusDto> fetchJobRecordsByFiltersPaginatedOutput(
      String jobId, String orgId, Optional<String> status, int pageNo, int pageSize) {

    Pageable element = PageRequest.of(pageNo - 1, pageSize, Sort.unsorted());
    Page<JobRecordEntity> entityPage =
        jobRecordRepository.findJobRecordsByJobParam(
            jobId, orgId, status.orElse(ApiStatusEnum.FAILURE.name()), element);

    return new PageImpl<>(
        JobRecordMapper.INSTANCE.toRecordStatusDtoList(entityPage.getContent()),
        entityPage.getPageable(),
        entityPage.getTotalElements());
  }
}
