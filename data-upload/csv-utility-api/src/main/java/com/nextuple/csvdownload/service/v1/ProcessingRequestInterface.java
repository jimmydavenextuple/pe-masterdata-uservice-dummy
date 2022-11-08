package com.nextuple.csvdownload.service.v1;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.csvdownload.common.inbound.GenericUploadRequest;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.outbound.PreSignedUrlResponse;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.util.Optional;

public interface ProcessingRequestInterface {

  String getModuleType();

  String submitJob(String orgId, long fileMetadataId) throws JobSubmissionException;

  void validate(GenericUploadRequest request, FileResponse fileResponse)
      throws CommonServiceException, CsvException, IOException;

  PreSignedUrlResponse downloadErrorLogs(JobDto jobDto, Optional<String> status)
      throws JobSubmissionException, IOException, CommonServiceException;

  JobTypeEnum getJobType();

  PreSignedUrlResponse downloadTransitTimeErrorLogs(JobDto jobDto, Optional<String> status)
      throws JobSubmissionException, IOException, CommonServiceException;
}
