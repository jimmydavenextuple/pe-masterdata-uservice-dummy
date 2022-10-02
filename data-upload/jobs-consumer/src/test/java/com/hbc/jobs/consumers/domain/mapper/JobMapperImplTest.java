package com.hbc.jobs.consumers.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.hbc.jobs.consumers.common.TestUtil;
import com.hbc.jobs.consumers.domain.entity.JobEntity;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.JobResponse;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JobMapperImplTest {

  @InjectMocks JobMapperImpl jobMapper;
  @InjectMocks TestUtil testUtil;

  @Test
  void toJobEntityList() {
    JobMapperImpl jobMapper = spy(JobMapperImpl.class);

    assertNull(null);
    verify(jobMapper, times(0)).toJobEntity(any());
  }

  @Test
  void updateJobEntityWithJobDto() {
    JobDto jobDto = mock(JobDto.class);
    JobEntity oldJobEntity = mock(JobEntity.class);

    JobMapperImpl jobMapper = spy(JobMapperImpl.class);

    JobEntity jobEntity = jobMapper.updateJobEntity(jobDto, oldJobEntity);

    Assertions.assertEquals(oldJobEntity, jobEntity);
  }

  @Test
  void updateJobEntityWithJobResponse() {
    JobResponse jobResponse =
        testUtil.createJobResponse(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);
    jobResponse.setProcessedRecords(1);
    jobResponse.setRemainingRecords(1);
    JobEntity oldJobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);

    JobEntity jobEntity = jobMapper.updateJobEntity(jobResponse, oldJobEntity);

    Assertions.assertNotNull(jobEntity.getFile());
  }

  @Test
  void toJobResponse() {
    JobEntity jobEntity = testUtil.createJobEntity(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 2);

    JobResponse jobResponse = jobMapper.toJobResponse(jobEntity);

    Assertions.assertNotNull(jobResponse);
  }
}
