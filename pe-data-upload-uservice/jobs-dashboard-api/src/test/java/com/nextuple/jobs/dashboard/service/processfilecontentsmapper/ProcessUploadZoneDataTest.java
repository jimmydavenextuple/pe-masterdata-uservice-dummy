/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.service.processfilecontentsmapper;

import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.jobs.consumers.authentication.AuthService;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class ProcessUploadZoneDataTest {

  @InjectMocks private ProcessUploadZoneData processUploadZoneData;
  @Mock private AuthService authService;
  @Mock private KafkaTemplate<String, Object> kafkaTemplate;
  @InjectMocks private TestUtil testUtil;

  @Test
  void updateRequestObjectsListTest() throws CommonServiceException, IOException, CsvException {
    InputStream inputStream = new ByteArrayInputStream(TestUtil.CSV_CONTENTS_ZONES.getBytes());
    List<Object> objectList =
        processUploadZoneData.updateRequestObjectsList(
            testUtil.getJobDto(JobTypeEnum.UPLOAD_ZONES), inputStream);

    Assertions.assertFalse(CollectionUtils.isEmpty(objectList));
    Assertions.assertEquals(9, objectList.size());
  }

  @Test
  void updateRequestObjectsListWithPartialEmptyCSVTest()
      throws CommonServiceException, IOException, CsvException {
    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.PARTIAL_EMPTY_CSV_CONTENTS_ZONES.getBytes());
    List<Object> objectList =
        processUploadZoneData.updateRequestObjectsList(
            testUtil.getJobDto(JobTypeEnum.UPLOAD_ZONES), inputStream);

    Assertions.assertFalse(CollectionUtils.isEmpty(objectList));
    Assertions.assertEquals(12, objectList.size());
  }

  @Test
  void updateRequestObjectsListWithEmptyCSVTest()
      throws CommonServiceException, IOException, CsvException {
    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.EMPTY_CSV_CONTENTS_ZONES.getBytes());
    List<Object> objectList =
        processUploadZoneData.updateRequestObjectsList(
            testUtil.getJobDto(JobTypeEnum.UPLOAD_ZONES), inputStream);

    Assertions.assertFalse(CollectionUtils.isEmpty(objectList));
    Assertions.assertEquals(3, objectList.size());
  }

  @Test
  void getJobTypeTest() {
    JobTypeEnum jobTypeEnum = processUploadZoneData.getJobType();
    Assertions.assertNotNull(jobTypeEnum);
    Assertions.assertEquals(JobTypeEnum.UPLOAD_ZONES, jobTypeEnum);
  }
}
