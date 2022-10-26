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
class ProcessTransitTimesUploadDataTest {

  @InjectMocks private ProcessTransitTimesUploadData processTransitTimesUploadData;

  @Test
  void updateRequestObjectsList() throws IOException, CsvException {
    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.CSV_CONTENTS_TRANSIT_TIMES.getBytes());
    List<Object> objectList =
        processTransitTimesUploadData.updateRequestObjectsList(
            JobTypeEnum.UPLOAD_TRANSIT_TIMES, inputStream);

    Assertions.assertFalse(CollectionUtils.isEmpty(objectList));
  }

  @Test
  void getJobType() {
    JobTypeEnum jobTypeEnum = processTransitTimesUploadData.getJobType();
    Assertions.assertNotNull(jobTypeEnum);
    Assertions.assertEquals(JobTypeEnum.UPLOAD_TRANSIT_TIMES, jobTypeEnum);
  }
}
