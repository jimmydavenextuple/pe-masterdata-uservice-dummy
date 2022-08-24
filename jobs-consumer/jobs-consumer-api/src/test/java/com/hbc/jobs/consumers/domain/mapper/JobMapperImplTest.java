package com.hbc.jobs.consumers.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.hbc.jobs.consumers.domain.entity.JobEntity;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JobMapperImplTest {

  @Test
  void toJobEntityList() {
    JobMapperImpl jobMapper = spy(JobMapperImpl.class);

    doReturn(null).when(jobMapper).toJobEntity(any());

    assertNull(null);
    verify(jobMapper, times(0)).toJobEntity(any());
  }

  @Test
  void updateJobEntity() {
    JobDto jobDto = mock(JobDto.class);
    JobEntity oldJobEntity = mock(JobEntity.class);

    JobMapperImpl jobMapper = spy(JobMapperImpl.class);

    JobEntity jobEntity = jobMapper.updateJobEntity(jobDto, oldJobEntity);

    Assertions.assertEquals(oldJobEntity, jobEntity);
  }
}
