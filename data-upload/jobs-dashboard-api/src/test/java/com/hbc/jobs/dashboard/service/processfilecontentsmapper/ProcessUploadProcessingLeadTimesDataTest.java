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
class ProcessUploadProcessingLeadTimesDataTest {

  @InjectMocks private ProcessUploadProcessingLeadTimesData processUploadProcessingLeadTimesData;

  @Test
  void updateRequestObjectsList() throws IOException, CsvException {
    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.CSV_CONTENTS_PROCESSING_LEAD_TIMES.getBytes());
    List<Object> objectList =
        processUploadProcessingLeadTimesData.updateRequestObjectsList(
            JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, inputStream);

    Assertions.assertFalse(CollectionUtils.isEmpty(objectList));
  }

  @Test
  void getJobType() {
    JobTypeEnum jobTypeEnum = processUploadProcessingLeadTimesData.getJobType();
    Assertions.assertNotNull(jobTypeEnum);
    Assertions.assertEquals(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, jobTypeEnum);
  }
}
