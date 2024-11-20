/*
 *
 *  * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *  *
 *  * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 *  * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 *
 */

package com.nextuple.jobs.consumers.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nextuple.jobs.framework.common.domain.outbound.JobResponseForNotification;
import com.nextuple.plt.domain.enums.JobStatusEnum;
import com.nextuple.plt.domain.outbound.JobResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JobsFpmMapperTest {

  private JobsFpmMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = Mappers.getMapper(JobsFpmMapper.class);
  }

  @Test
  @DisplayName("Mapping JobResponse to JobResponseForNotification")
  void testToJobResponseForNotification_JobResponse() {
    JobResponse jobResponse = mock(JobResponse.class);
    when(jobResponse.getStatus()).thenReturn(JobStatusEnum.COMPLETED);
    JobResponseForNotification result = mapper.toJobResponseForNotification(jobResponse);
    assertEquals(null, result.getMetadata());
  }

  @Test
  @DisplayName("Mapping PltJobResponse to JobResponseForNotification")
  void testToJobResponseForNotification_PltJobResponse() {
    JobResponse pltJobResponse = mock(JobResponse.class);
    when(pltJobResponse.getStatus()).thenReturn(JobStatusEnum.PROCESSING);
    JobResponseForNotification result = mapper.toJobResponseForNotification(pltJobResponse);
    assertEquals(null, result.getMetadata());
  }
}
