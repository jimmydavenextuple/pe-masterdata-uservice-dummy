package com.nextuple.jobs.dashboard.service;

import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ProcessFileContents {

  JobTypeEnum getJobType();

  List<Object> updateRequestObjectsList(JobTypeEnum jobType, InputStream inputStream)
      throws IOException, CsvException;
}
