package com.hbc.jobs.dashboard.service.processfilecontentsmapper;

import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
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
class ProcessUploadPickupCalendarDataTest {

  @InjectMocks private ProcessUploadPickupCalendarData processUploadPickupCalendarData;

  @Test
  void updateRequestObjectsList() throws IOException, CsvException {
    Path resourceDirectory = Paths.get("src", "test", "resources", "calendar", "calendar.csv");
    InputStream inputStream = new ByteArrayInputStream(Files.readAllBytes(resourceDirectory));

    List<Object> objectList =
        processUploadPickupCalendarData.updateRequestObjectsList(
            JobTypeEnum.UPLOAD_PICKUP_CALENDER, inputStream);

    Assertions.assertFalse(CollectionUtils.isEmpty(objectList));
  }

  @Test
  void getJobType() {
    JobTypeEnum jobTypeEnum = processUploadPickupCalendarData.getJobType();
    Assertions.assertNotNull(jobTypeEnum);
    Assertions.assertEquals(JobTypeEnum.UPLOAD_PICKUP_CALENDER, jobTypeEnum);
  }
}
