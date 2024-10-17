/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.dashboard.service.processfilecontentsmapper;

import com.nextuple.jobs.dashboard.common.TestUtil;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.opencsv.exceptions.CsvException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class ProcessUploadProcessingTimeBufferDataTest {

  @InjectMocks private ProcessUploadProcessingTimeBufferData processUploadProcessingTimeBufferData;
  @InjectMocks private TestUtil testUtil;

  @Test
  void updateRequestObjectsList() throws IOException, CsvException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeserviceoptionbuffer",
            "node-service-option-buffer.csv");
    InputStream inputStream = new ByteArrayInputStream(Files.readAllBytes(resourceDirectory));

    List<Object> objectList =
        processUploadProcessingTimeBufferData.updateRequestObjectsList(
            testUtil.getJobDto(JobTypeEnum.UPLOAD_NODE_SERVICE_OPTION_BUFFER), inputStream);

    Assertions.assertFalse(CollectionUtils.isEmpty(objectList));
  }

  @Test
  void getJobType() {
    JobTypeEnum jobTypeEnum = processUploadProcessingTimeBufferData.getJobType();
    Assertions.assertNotNull(jobTypeEnum);
    Assertions.assertEquals(JobTypeEnum.UPLOAD_NODE_SERVICE_OPTION_BUFFER, jobTypeEnum);
  }
}
