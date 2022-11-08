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
class ProcessUploadCarrierServiceDataTest {

  @InjectMocks private ProcessUploadCarrierServiceData processUploadCarrierServiceData;

  @Test
  void updateRequestObjectsList() throws IOException, CsvException {
    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.CSV_CONTENTS_UPLOAD_CARRIER_SERVICE.getBytes());
    List<Object> objectList =
        processUploadCarrierServiceData.updateRequestObjectsList(
            JobTypeEnum.UPLOAD_CARRIER_SERVICE, inputStream);

    Assertions.assertFalse(CollectionUtils.isEmpty(objectList));
  }

  @Test
  void getJobType() {
    JobTypeEnum jobTypeEnum = processUploadCarrierServiceData.getJobType();
    Assertions.assertNotNull(jobTypeEnum);
    Assertions.assertEquals(JobTypeEnum.UPLOAD_CARRIER_SERVICE, jobTypeEnum);
  }
}
