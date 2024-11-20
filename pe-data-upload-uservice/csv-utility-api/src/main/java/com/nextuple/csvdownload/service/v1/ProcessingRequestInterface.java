/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service.v1;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.csvdownload.common.inbound.GenericUploadRequest;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.opencsv.exceptions.CsvException;
import java.io.File;
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

  PreSignedUrlResponse generateURLResponse(File csv, JobDto jobDto, String feedType)
      throws CommonServiceException, IOException;
}
