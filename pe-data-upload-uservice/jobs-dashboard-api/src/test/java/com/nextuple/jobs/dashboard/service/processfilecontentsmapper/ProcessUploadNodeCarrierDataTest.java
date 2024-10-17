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
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class ProcessUploadNodeCarrierDataTest {

  @InjectMocks private ProcessUploadNodeCarrierData processUploadNodeCarrierData;
  @InjectMocks private TestUtil testUtil;

  @Test
  void updateRequestObjectsList() throws IOException, CsvException {
    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.CSV_CONTENTS_UPLOAD_NODE_CARRIER.getBytes());
    List<Object> objectList =
        processUploadNodeCarrierData.updateRequestObjectsList(
            testUtil.getJobDto(JobTypeEnum.UPLOAD_NODE_CARRIER), inputStream);

    Assertions.assertFalse(CollectionUtils.isEmpty(objectList));
  }

  @Test
  void getJobType() {
    JobTypeEnum jobTypeEnum = processUploadNodeCarrierData.getJobType();
    Assertions.assertNotNull(jobTypeEnum);
    Assertions.assertEquals(JobTypeEnum.UPLOAD_NODE_CARRIER, jobTypeEnum);
  }
}
