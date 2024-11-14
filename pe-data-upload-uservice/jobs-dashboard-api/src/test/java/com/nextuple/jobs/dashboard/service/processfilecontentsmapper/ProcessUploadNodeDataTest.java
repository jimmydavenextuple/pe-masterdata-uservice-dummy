/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.service.processfilecontentsmapper;

import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.dataupload.common.config.TenantDatabaseConfig;
import com.nextuple.jobs.dashboard.common.TestUtil;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.opencsv.exceptions.CsvException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class ProcessUploadNodeDataTest {

  @InjectMocks private ProcessUploadNodeData processUploadNodeData;
  @Mock private TenantDatabaseConfig tenantDatabaseConfig;
  @InjectMocks private TestUtil testUtil;

  @Test
  void updateRequestObjectsList() throws IOException, CsvException, CommonServiceException {
    when(tenantDatabaseConfig.getCurrentTenantServiceOptions())
        .thenReturn(TestUtil.tenantServiceOptionExpected.toArray(new String[0]));

    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.CSV_CONTENTS_UPLOAD_NODE.getBytes());
    List<Object> objectList =
        processUploadNodeData.updateRequestObjectsList(
            testUtil.getJobDto(JobTypeEnum.UPLOAD_NODES), inputStream);

    Assertions.assertFalse(CollectionUtils.isEmpty(objectList));
  }

  @Test
  void getJobType() {
    JobTypeEnum jobTypeEnum = processUploadNodeData.getJobType();
    Assertions.assertNotNull(jobTypeEnum);
    Assertions.assertEquals(JobTypeEnum.UPLOAD_NODES, jobTypeEnum);
  }

  @Test
  void updateRequestObjectsListWhenNoServiceOptionsAreConfiguredExceptionTest()
      throws CommonServiceException {
    when(tenantDatabaseConfig.getCurrentTenantServiceOptions())
        .thenThrow(new CommonServiceException(HttpStatus.NOT_FOUND, 0x1776, null));

    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.CSV_CONTENTS_UPLOAD_NODE.getBytes());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                processUploadNodeData.updateRequestObjectsList(
                    testUtil.getJobDto(JobTypeEnum.UPLOAD_NODES), inputStream));

    Assertions.assertNotNull(exception);
  }
}
