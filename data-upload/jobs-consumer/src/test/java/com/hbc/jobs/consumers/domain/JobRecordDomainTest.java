package com.hbc.jobs.consumers.domain;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.jobs.consumers.common.TestUtil;
import com.hbc.jobs.consumers.domain.entity.JobRecordEntity;
import com.hbc.jobs.consumers.domain.repository.JobRecordRepository;
import com.hbc.jobs.consumers.exception.JobRecordDomainException;
import com.hbc.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class JobRecordDomainTest {

  @Mock private JobRecordRepository jobRecordRepository;

  @InjectMocks private JobRecordDomain jobRecordDomain;

  @InjectMocks private TestUtil testUtil;

  @Test
  void create() throws JobRecordDomainException {
    JobRecordEntity jobRecordEntity = mock(JobRecordEntity.class);

    when(jobRecordRepository.save(any(JobRecordEntity.class))).thenReturn(jobRecordEntity);

    JobRecordEntity jobRecordEntity1 = jobRecordDomain.create(jobRecordEntity);
    Assertions.assertEquals(jobRecordEntity, jobRecordEntity1);
    verify(jobRecordRepository, times(1)).save(any(JobRecordEntity.class));
  }

  @Test
  void createError() {
    JobRecordEntity jobRecordEntity = mock(JobRecordEntity.class);

    when(jobRecordRepository.save(any(JobRecordEntity.class))).thenThrow(RuntimeException.class);

    JobRecordDomainException e =
        assertThrows(JobRecordDomainException.class, () -> jobRecordDomain.create(jobRecordEntity));
    Assertions.assertEquals("Exception while persisting the job record", e.getMessage());
    assertNull(e.getJobRecordId());
    verify(jobRecordRepository, times(1)).save(any(JobRecordEntity.class));
  }

  @Test
  void findConsumerJobsByJobParam() {
    when(jobRecordRepository.findJobRecordsByFilters(any(), any(), any()))
        .thenReturn(Collections.singletonList(testUtil.getJobRecordEntity()));
    List<RecordStatusDto> recordStatusDtos =
        jobRecordDomain.findConsumerJobsByJobParam(
            TestUtil.ORG_ID, "jobId", Optional.of("activateStatus"));

    Assertions.assertFalse(CollectionUtils.isEmpty(recordStatusDtos));
  }

  @Test
  void fetchJobRecordsByFiltersPaginatedOutput() {
    Pageable pageable = PageRequest.of(0, 2, Sort.unsorted());

    when(jobRecordRepository.findJobRecordsByJobParam(anyString(), anyString(), any(), any()))
        .thenReturn(new PageImpl<>(List.of(testUtil.getJobRecordEntity()), pageable, 10));

    Page<RecordStatusDto> recordStatusDtoPage =
        jobRecordDomain.fetchJobRecordsByFiltersPaginatedOutput(
            TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.SUCCESS.name()), 1, 20);

    Assertions.assertNotNull(recordStatusDtoPage);
    Assertions.assertFalse(CollectionUtils.isEmpty(recordStatusDtoPage.getContent()));
  }
}
