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
class ProcessNodeCalendarUploadDataTest {

  @InjectMocks private ProcessNodeCalendarUploadData processNodeCalendarUploadData;

  @Test
  void updateRequestObjectsList() throws IOException, CsvException {
    InputStream inputStream =
        new ByteArrayInputStream(TestUtil.CSV_CONTENTS_UPLOAD_NODE_CALENDAR.getBytes());
    List<Object> objectList =
        processNodeCalendarUploadData.updateRequestObjectsList(
            JobTypeEnum.UPLOAD_NODE_CALENDER, inputStream);

    Assertions.assertFalse(CollectionUtils.isEmpty(objectList));
  }

  @Test
  void getJobType() {
    JobTypeEnum jobTypeEnum = processNodeCalendarUploadData.getJobType();
    Assertions.assertNotNull(jobTypeEnum);
    Assertions.assertEquals(JobTypeEnum.UPLOAD_NODE_CALENDER, jobTypeEnum);
  }
}
