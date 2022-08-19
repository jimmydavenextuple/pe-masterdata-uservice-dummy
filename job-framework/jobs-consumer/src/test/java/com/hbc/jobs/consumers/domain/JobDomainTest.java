package com.hbc.jobs.consumers.domain;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.jobs.consumers.common.TestUtil;
import com.hbc.jobs.consumers.domain.entity.JobEntity;
import com.hbc.jobs.consumers.domain.repository.JobRepository;
import com.hbc.jobs.consumers.exception.JobDomainException;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import java.util.Collections;
import java.util.Date;
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

    Page<JobDto> jobDtos =
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
}
