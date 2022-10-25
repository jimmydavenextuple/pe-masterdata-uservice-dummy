package com.hbc.csvdownload.service.v1;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.csvdownload.common.inbound.GenericUploadRequest;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.jobs.framework.common.domain.outbound.FileResponse;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;

public interface ProcessingRequestInterface {

  String getModuleType();

  String submitJob(String orgId, long fileMetadataId) throws JobSubmissionException;

  void validate(GenericUploadRequest request, FileResponse fileResponse)
      throws CommonServiceException, CsvException, IOException;
}
