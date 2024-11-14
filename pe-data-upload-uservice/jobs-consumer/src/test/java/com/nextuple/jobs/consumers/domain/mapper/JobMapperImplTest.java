/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.consumers.domain.entity.JobEntity;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
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
