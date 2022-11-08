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
class ProcessDeleteTransitBufferUploadDataTest {

  @InjectMocks private ProcessDeleteTransitBufferUploadData processDeleteTransitBufferUploadData;

  @Test
  void updateRequestObjectsList() throws IOException, CsvException {
    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.CSV_CONTENTS_DELETE_TRANSIT_BUFFER.getBytes());
    List<Object> objectList =
        processDeleteTransitBufferUploadData.updateRequestObjectsList(
            JobTypeEnum.DELETE_TRANSIT_BUFFER, inputStream);
    Assertions.assertFalse(CollectionUtils.isEmpty(objectList));
  }

  @Test
  void getJobType() {
    JobTypeEnum jobTypeEnum = processDeleteTransitBufferUploadData.getJobType();
    Assertions.assertNotNull(jobTypeEnum);
    Assertions.assertEquals(JobTypeEnum.DELETE_TRANSIT_BUFFER, jobTypeEnum);
  }
}
