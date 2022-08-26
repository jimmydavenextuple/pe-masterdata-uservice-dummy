package com.hbc.jobs.consumers.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.jobs.consumers.domain.entity.JobRecordEntity;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JobRecordMapperImplTest {

  @InjectMocks private JobRecordMapperImpl jobRecordMapper;

  @Test
  void toRecordStatus() {
    JobRecordEntity jobRecordEntity = mock(JobRecordEntity.class);

    RecordStatusDto recordStatusDto = jobRecordMapper.toRecordStatus(jobRecordEntity);

    assertNotNull(recordStatusDto);
    assertNull(recordStatusDto.getJobId());
  }

  @Test
  void toJobRecordEntityList() {
    JobRecordMapperImpl jobRecordMapper = spy(JobRecordMapperImpl.class);

    when(jobRecordMapper.toJobRecordEntity(any())).thenReturn(null);

    RecordStatusDto recordStatusDto = mock(RecordStatusDto.class);
    List<RecordStatusDto> recordStatusDtos =
        new ArrayList<>(Collections.singletonList(recordStatusDto));
    List<JobRecordEntity> jobRecordEntities =
        jobRecordMapper.toJobRecordEntityList(recordStatusDtos);

    assertEquals(1, jobRecordEntities.size());
    verify(jobRecordMapper, times(1)).toJobRecordEntity(any());
  }
}
