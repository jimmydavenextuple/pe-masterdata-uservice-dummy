package com.hbc.jobs.dashboard.service.processfilecontentsmapper;

import com.hbc.jobs.dashboard.common.TestUtil;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
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

  @Test
  void updateRequestObjectsList() throws IOException, CsvException {
    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.CSV_CONTENTS_UPLOAD_NODE_CARRIER.getBytes());
    List<Object> objectList =
        processUploadNodeCarrierData.updateRequestObjectsList(
            JobTypeEnum.UPLOAD_NODE_CARRIER, inputStream);

    Assertions.assertFalse(CollectionUtils.isEmpty(objectList));
  }

  @Test
  void getJobType() {
    JobTypeEnum jobTypeEnum = processUploadNodeCarrierData.getJobType();
    Assertions.assertNotNull(jobTypeEnum);
    Assertions.assertEquals(JobTypeEnum.UPLOAD_NODE_CARRIER, jobTypeEnum);
  }
}
