package com.hbc.csvdownload.service.v1.impl;

import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.csvdownload.util.TestUtil;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.FileResponse;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProcessingLeadTimesProcessingRequestImplTest {
  @InjectMocks ProcessingLeadTimesProcessingRequestImpl processingLeadTimesProcessingRequest;

  @Spy JobsDashboardClient jobsDashboardClient;

  @InjectMocks private TestUtil testUtil;

  @Test
  void submitJobTest() throws JobSubmissionException {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, TestUtil.FILE_METADATA_ID))
        .thenReturn(BaseResponse.builder().payload(testUtil.getJobResponse()).build());
    String result =
        processingLeadTimesProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID);
    Assertions.assertEquals(TestUtil.JOB_ID, result);
  }

  @Test
  void submitJobFeignExceptionTest() {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, TestUtil.FILE_METADATA_ID))
        .thenThrow(FeignException.class);
    Assertions.assertThrows(
        JobSubmissionException.class,
        () ->
            processingLeadTimesProcessingRequest.submitJob(
                TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID));
  }

  @Test
  void submitJobExceptionTest() {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, TestUtil.FILE_METADATA_ID))
        .thenThrow(ArithmeticException.class);
    Assertions.assertThrows(
        JobSubmissionException.class,
        () ->
            processingLeadTimesProcessingRequest.submitJob(
                TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID));
  }

  @Test
  void validateCsvTest() throws IOException, CommonServiceException, CsvException {
    Path path =
        Paths.get("src", "test", "resources", "processingLeadTimes", "processingLeadTimes.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setFileName("processingLeadTimes.csv");
    response.setInputStream(inputStream);
    processingLeadTimesProcessingRequest.validate(testUtil.getGenericUploadRequest(), response);
    Assertions.assertEquals("processingLeadTimes.csv", response.getFileName());
  }

  @Test
  void validateInvalidHeadersExceptionTest() throws IOException {
    Path path =
        Paths.get(
            "src",
            "test",
            "resources",
            "processingLeadTimes",
            "processingLeadTimesInvalidHeaders.csv");
    FileResponse response = getFileResponseWithInputStream(path);
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                processingLeadTimesProcessingRequest.validate(
                    testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals(
        "Processing lead times data uploaded file has invalid headers.", ex.getMessage());
  }

  @Test
  void validateEmptyCsvExceptionTest() throws IOException {
    Path path =
        Paths.get(
            "src", "test", "resources", "processingLeadTimes", "processingLeadTimesEmpty.csv");
    FileResponse response = getFileResponseWithInputStream(path);
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                processingLeadTimesProcessingRequest.validate(
                    testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals("No Records found in the csv", ex.getMessage());
  }

  @Test
  void validateFullEmptyCsvExceptionTest() throws IOException {
    Path path =
        Paths.get(
            "src", "test", "resources", "processingLeadTimes", "processingLeadTimesFullEmpty.csv");
    FileResponse fileResponse = getFileResponseWithInputStream(path);
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                processingLeadTimesProcessingRequest.validate(
                    testUtil.getGenericUploadRequest(), fileResponse));
    Assertions.assertEquals("No Records found in the csv", ex.getMessage());
  }

  @Test
  void validateInvalidFileTypeExceptionTest() {
    FileResponse response = testUtil.getFileResponse();
    response.setContentType("");
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                processingLeadTimesProcessingRequest.validate(
                    testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals(
        "Processing lead times data uploaded file has invalid file type.", ex.getMessage());
  }

  private FileResponse getFileResponseWithInputStream(Path path) throws IOException {
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    return response;
  }
}
