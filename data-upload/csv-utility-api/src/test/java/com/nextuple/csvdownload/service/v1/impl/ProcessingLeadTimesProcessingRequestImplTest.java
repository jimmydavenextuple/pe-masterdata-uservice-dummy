package com.nextuple.csvdownload.service.v1.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.csvdownload.util.TestUtil;
import com.nextuple.jobs.framework.common.clients.FileMetaDataClient;
import com.nextuple.jobs.framework.common.clients.JobsDashboardClient;
import com.nextuple.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.outbound.PreSignedUrlResponse;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.nextuple.jobs.framework.common.service.FileService;
import com.nextuple.jobs.framework.common.service.PreSignedUrlInterface;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ObjectUtils;

@ExtendWith(MockitoExtension.class)
class ProcessingLeadTimesProcessingRequestImplTest {
  @InjectMocks ProcessingLeadTimesProcessingRequestImpl processingLeadTimesProcessingRequest;

  @Spy JobsDashboardClient jobsDashboardClient;

  @InjectMocks private TestUtil testUtil;
  @Mock private FileService fileService;
  @Mock private FileMetaDataClient fileMetaDataClient;
  @Mock private PreSignedUrlInterface preSignedUrlInterface;

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(processingLeadTimesProcessingRequest, "bucketName", "bucket");
    ReflectionTestUtils.setField(processingLeadTimesProcessingRequest, "storageType", "s3");
  }

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

  @Test
  void downloadErrorLogsTest() throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto =
        testUtil.createJob(TestUtil.JOB_ID, JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, 20);
    List<RecordStatusDto> recordStatusDto = testUtil.getJobRecordsForProcessingLeadTimes();

    PagePayload<RecordStatusDto> recordStatusDtoPagePayload =
        testUtil.createPagePayloadRecordStatusDto(recordStatusDto, 5, 20, 1);

    when(jobsDashboardClient.getJobRecordsByFilters(any(), any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(recordStatusDtoPagePayload).build());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());

    when(preSignedUrlInterface.downloadFileURLById(1L))
        .thenReturn(testUtil.getPreSignedUrlResponse());
    when(fileMetaDataClient.createFileMetadata(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getFileMetaDataResponse()).build());

    PreSignedUrlResponse response =
        processingLeadTimesProcessingRequest.downloadErrorLogs(
            jobDto, Optional.of(ApiStatusEnum.FAILURE.name()));

    Assertions.assertNotNull(response);
    Assertions.assertFalse(ObjectUtils.isEmpty(response));
  }

  @Test
  void getJobType() {
    JobTypeEnum jobTypeEnum = processingLeadTimesProcessingRequest.getJobType();
    Assertions.assertEquals(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES, jobTypeEnum);
  }
}
