/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.domain;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.consumers.domain.entity.JobEntity;
import com.nextuple.jobs.consumers.domain.repository.JobRepository;
import com.nextuple.jobs.consumers.exception.JobDomainException;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class JobDomainTest {

  @Mock private JobRepository jobRepository;

  @InjectMocks private JobDomain jobDomain;

  @InjectMocks private TestUtil testUtil;

  @Test
  void create() throws JobDomainException {
    JobEntity jobEntity = mock(JobEntity.class);

    when(jobRepository.save(any(JobEntity.class))).thenReturn(jobEntity);

    JobEntity jobEntity1 = jobDomain.save(jobEntity);

    Assertions.assertEquals(jobEntity, jobEntity1);
    verify(jobRepository, times(1)).save(any(JobEntity.class));
  }

  @Test
  void createError() {
    JobEntity jobEntity = mock(JobEntity.class);

    when(jobRepository.save(any(JobEntity.class))).thenThrow(RuntimeException.class);

    JobDomainException e =
        Assertions.assertThrows(JobDomainException.class, () -> jobDomain.save(jobEntity));
    Assertions.assertNull(e.getJobId());
    verify(jobRepository, times(1)).save(any(JobEntity.class));
  }

  @Test
  void findByJobIdAndTenantId() throws JobDomainException {
    Optional<JobEntity> jobEntity = Optional.of(mock(JobEntity.class));

    when(jobRepository.findJobByJobIdAndOrgId(any(), any())).thenReturn(jobEntity);

    JobEntity jobEntity1 = jobDomain.findJobByJobIdAndOrgId(null, null);

    Assertions.assertEquals(jobEntity.get(), jobEntity1);
    verify(jobRepository, times(1)).findJobByJobIdAndOrgId(any(), any());
  }

  @Test
  void findByJobIdAndTenantIdError() {
    when(jobRepository.findJobByJobIdAndOrgId(any(), any())).thenThrow(RuntimeException.class);

    JobDomainException e =
        Assertions.assertThrows(
            JobDomainException.class, () -> jobDomain.findJobByJobIdAndOrgId(null, null));
    Assertions.assertEquals("Exception while retrieving the job", e.getMessage());
    Assertions.assertNull(e.getJobId());
    verify(jobRepository, times(1)).findJobByJobIdAndOrgId(any(), any());
  }

  @Test
  void save() throws JobDomainException {
    JobEntity jobEntity = mock(JobEntity.class);

    when(jobRepository.save(any(JobEntity.class))).thenReturn(jobEntity);

    JobEntity jobEntity1 = jobDomain.save(jobEntity);

    Assertions.assertEquals(jobEntity, jobEntity1);
    verify(jobRepository, times(1)).save(any(JobEntity.class));
  }

  @Test
  void saveError() {
    JobEntity jobEntity = mock(JobEntity.class);

    when(jobRepository.save(any(JobEntity.class))).thenThrow(RuntimeException.class);

    JobDomainException e =
        Assertions.assertThrows(JobDomainException.class, () -> jobDomain.save(jobEntity));
    Assertions.assertEquals("Exception while saving the job", e.getMessage());
    Assertions.assertNull(e.getJobId());
    verify(jobRepository, times(1)).save(any(JobEntity.class));
  }

  @Test
  void findJobsByJobParam() {
    Sort sort =
        Sort.by(
            Sort.Direction.fromOptionalString("").orElse(Sort.DEFAULT_DIRECTION), "created_date");

    Pageable pageable = PageRequest.of(0, 2, sort);

    when(jobRepository.findJobsByJobParam(any(), any(), any(), any()))
        .thenReturn(
            new PageImpl<>(
                Collections.singletonList(
                    testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 10)),
                pageable,
                10));

    Page<JobResponse> jobDtos =
        jobDomain.findJobsByJobParam(
            TestUtil.ORG_ID,
            Optional.of("testType"),
            Optional.of(new Date()),
            TestUtil.DEFAULT_SORT_FIELD.orElse(""),
            TestUtil.DEFAULT_SORT_ORDER.orElse(""),
            1,
            2);

    Assertions.assertEquals(1, jobDtos.getContent().size());
    verify(jobRepository, times(1)).findJobsByJobParam(any(), any(), any(), any());
  }

  @Test
  void updateJobStatusByOrgIdAndStatus() throws JobDomainException {
    JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    jobEntity.setStatus(JobStatusEnum.SUBMITTED);
    when(jobRepository.getJobStatusByStatus(any())).thenReturn(jobEntity);
    jobEntity.setStatus(JobStatusEnum.PROCESSING);

    when(jobRepository.save(any())).thenReturn(jobEntity);

    JobEntity jobEntity1 =
        jobDomain.getAndUpdateJobStatusByStatus(JobStatusEnum.SUBMITTED, JobStatusEnum.PROCESSING);

    Assertions.assertNotNull(jobEntity1);

    verify(jobRepository, times(1)).getJobStatusByStatus(any());
    verify(jobRepository, times(1)).save(any());
  }

  @Test
  void updateJobStatusByOrgIdAndStatusNullJobDto() throws JobDomainException {

    when(jobRepository.getJobStatusByStatus(any())).thenReturn(null);

    JobEntity jobEntity2 =
        jobDomain.getAndUpdateJobStatusByStatus(JobStatusEnum.SUBMITTED, JobStatusEnum.PROCESSING);

    Assertions.assertNull(jobEntity2);

    verify(jobRepository, times(1)).getJobStatusByStatus(any());
  }

  @Test
  void updateJobStatusByOrgIdAndStatusException() {
    JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    jobEntity.setStatus(JobStatusEnum.SUBMITTED);
    when(jobRepository.getJobStatusByStatus(any())).thenReturn(jobEntity);
    jobEntity.setStatus(JobStatusEnum.PROCESSING);
    when(jobRepository.save(jobEntity))
        .thenThrow(new RuntimeException("Exception while retrieving the job"));

    Exception exception =
        Assertions.assertThrows(
            JobDomainException.class,
            () ->
                jobDomain.getAndUpdateJobStatusByStatus(
                    JobStatusEnum.SUBMITTED, JobStatusEnum.PROCESSING));

    Assertions.assertNotNull(exception);
  }

  @Test
  @DisplayName(
      "Update job status by finding it by jobId, orgId, oldStatus and updating it with newStatus")
  void updateJobStatusByJobIdAndOrgIdAndStatus() throws JobDomainException {
    JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    jobEntity.setStatus(JobStatusEnum.SUBMITTED);
    when(jobRepository.findJobByJobIdAndOrgIdAndStatus(any(), any(), any())).thenReturn(jobEntity);
    jobEntity.setStatus(JobStatusEnum.PROCESSING);

    when(jobRepository.save(any())).thenReturn(jobEntity);

    JobEntity jobEntity1 =
        jobDomain.getAndUpdateJobStatusByStatus(
            jobEntity.getJobId(),
            jobEntity.getOrgId(),
            JobStatusEnum.SUBMITTED,
            JobStatusEnum.PROCESSING);

    Assertions.assertNotNull(jobEntity1);

    verify(jobRepository, times(1)).findJobByJobIdAndOrgIdAndStatus(any(), any(), any());
    verify(jobRepository, times(1)).save(any());
  }

  @Test
  @DisplayName(
      "Exception while updating job status by finding it by jobId, orgId, oldStatus and updating it with newStatus")
  void updateJobStatusByJobIdAndOrgIdAndStatusException() {
    JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5);
    jobEntity.setStatus(JobStatusEnum.SUBMITTED);
    when(jobRepository.findJobByJobIdAndOrgIdAndStatus(any(), any(), any())).thenReturn(jobEntity);
    jobEntity.setStatus(JobStatusEnum.PROCESSING);
    when(jobRepository.save(jobEntity))
        .thenThrow(new RuntimeException("Exception while retrieving the job"));

    Exception exception =
        Assertions.assertThrows(
            JobDomainException.class,
            () ->
                jobDomain.getAndUpdateJobStatusByStatus(
                    jobEntity.getJobId(),
                    jobEntity.getOrgId(),
                    JobStatusEnum.SUBMITTED,
                    JobStatusEnum.PROCESSING));

    Assertions.assertNotNull(exception);
    verify(jobRepository, times(1)).findJobByJobIdAndOrgIdAndStatus(any(), any(), any());
  }

  @Test
  void fetchJobRecordInTimeRange() throws JobDomainException {
    when(jobRepository.fetchJobRecordInTimeRange(any(), any()))
        .thenReturn(testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 5));

    JobEntity jobEntity =
        jobDomain.fetchJobRecordInTimeRange(JobStatusEnum.PROCESSING.name(), new Date());
    Assertions.assertNotNull(jobEntity);
    verify(jobRepository, times(1)).fetchJobRecordInTimeRange(any(), any());
  }

  @Test
  void fetchJobRecordInTimeRangeException() {
    when(jobRepository.fetchJobRecordInTimeRange(any(), any())).thenThrow(new RuntimeException(""));
    Exception exception =
        Assertions.assertThrows(
            JobDomainException.class,
            () -> jobDomain.fetchJobRecordInTimeRange(JobStatusEnum.PROCESSING.name(), new Date()));
    Assertions.assertNotNull(exception);
  }
}
