package com.nextuple.csvdownload.service.v1.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.jobs.framework.common.inbound.FileMetaDataCreationRequest;
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
class PostalCodeTimezoneProcessingRequestImplTest {

  @InjectMocks PostalCodeTimezoneProcessingRequestImpl postalCodeTimezoneProcessingRequest;

  @Spy JobsDashboardClient jobsDashboardClient;

  @InjectMocks private TestUtil testUtil;

  @Mock FileService fileService;

  @Mock PreSignedUrlInterface preSignedUrlInterface;

  @Mock FileMetaDataClient fileMetaDataClient;

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(postalCodeTimezoneProcessingRequest, "bucketName", "bucket");
    ReflectionTestUtils.setField(postalCodeTimezoneProcessingRequest, "storageType", "s3");
  }

  @Test
  void submitJobTest() throws JobSubmissionException {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_POSTAL_CODE_TIMEZONE, TestUtil.FILE_METADATA_ID))
        .thenReturn(BaseResponse.builder().payload(testUtil.getJobResponse()).build());
    String result =
        postalCodeTimezoneProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID);
    Assertions.assertEquals(TestUtil.JOB_ID, result);
  }

  @Test
  void submitJobFeignExceptionTest() {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_POSTAL_CODE_TIMEZONE, TestUtil.FILE_METADATA_ID))
        .thenThrow(FeignException.class);
    Assertions.assertThrows(
        JobSubmissionException.class,
        () ->
            postalCodeTimezoneProcessingRequest.submitJob(
                TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID));
  }

  @Test
  void submitJobExceptionTest() {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_POSTAL_CODE_TIMEZONE, TestUtil.FILE_METADATA_ID))
        .thenThrow(ArithmeticException.class);
    Assertions.assertThrows(
        JobSubmissionException.class,
        () ->
            postalCodeTimezoneProcessingRequest.submitJob(
                TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID));
  }

  @Test
  void validateCsvTest() throws IOException, CommonServiceException, CsvException {
    Path path =
        Paths.get("src", "test", "resources", "postalCodeTimezone", "postalCodeTimezone.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    postalCodeTimezoneProcessingRequest.validate(testUtil.getGenericUploadRequest(), response);
    Assertions.assertEquals("postalCodeTimezone.csv", response.getFileName());
  }

  @Test
  void validateInvalidHeadersExceptionTest() throws IOException {
    Path path =
        Paths.get(
            "src",
            "test",
            "resources",
            "postalCodeTimezone",
            "postalCodeTimezoneInvalidHeader.csv");
    FileResponse response = getFileResponseWithInputStream(path);
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                postalCodeTimezoneProcessingRequest.validate(
                    testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals(
        "Market Region data uploaded file has invalid headers.", ex.getMessage());
  }

  @Test
  void validateEmptyCsvExceptionTest() throws IOException {
    Path path =
        Paths.get("src", "test", "resources", "postalCodeTimezone", "postalCodeTimezoneEmpty.csv");
    FileResponse response = getFileResponseWithInputStream(path);
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                postalCodeTimezoneProcessingRequest.validate(
                    testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals("No Records found in the csv", ex.getMessage());
  }

  @Test
  void validateFullEmptyCsvExceptionTest() throws IOException {
    Path path =
        Paths.get(
            "src", "test", "resources", "postalCodeTimezone", "postalCodeTimezoneFullEmpty.csv");
    FileResponse fileResponse = getFileResponseWithInputStream(path);
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                postalCodeTimezoneProcessingRequest.validate(
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
                postalCodeTimezoneProcessingRequest.validate(
                    testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals(
        "Market Region data uploaded file has invalid file type.", ex.getMessage());
  }

  @Test
  void downloadErrorLogs() throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto =
        testUtil.createJob(TestUtil.JOB_ID, JobTypeEnum.UPLOAD_POSTAL_CODE_TIMEZONE, 20);
    FileMetaDataCreationRequest request =
        FileMetaDataCreationRequest.builder()
            .name("download-log-postal-code-timezone16673736661972681969085673741863.csv")
            .path(
                String.format(
                    "bucket/ui-logs/postal-code-timezone/2022-11-02/download-log-postal-code-timezone16673736661972681969085673741863.csv"))
            .size(String.valueOf(0))
            .description("Error log file metadata")
            .storageType("s3")
            .extReferenceId(jobDto.getJobId())
            .type(ModuleEnum.UI_LOGS.getModuleValue())
            .createdBy("SYSTEM")
            .build();
    RecordStatusDto recordStatusDto = testUtil.getRecordStatusDtoForPostalCodeTimeZone();
    when(jobsDashboardClient.getJobRecordsByFilters(
            anyString(), anyString(), anyString(), anyInt(), anyInt()))
        .thenReturn(
            BaseResponse.builder()
                .payload(
                    testUtil.createPagePayloadRecordStatusDto(List.of(recordStatusDto), 5, 20, 1))
                .build());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());
    when(preSignedUrlInterface.downloadFileURLById(1L))
        .thenReturn(testUtil.getPreSignedUrlResponse());
    when(fileMetaDataClient.createFileMetadata(request))
        .thenReturn(BaseResponse.builder().payload(testUtil.getFileMetaDataResponse()).build());

    PreSignedUrlResponse preSignedUrlResponse =
        postalCodeTimezoneProcessingRequest.downloadErrorLogs(
            jobDto, Optional.of(ApiStatusEnum.FAILURE.name()));

    Assertions.assertNotNull(preSignedUrlResponse);
    Assertions.assertFalse(ObjectUtils.isEmpty(preSignedUrlResponse));
  }

  private FileResponse getFileResponseWithInputStream(Path path) throws IOException {
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    return response;
  }

  @Test
  void getJobType() {
    JobTypeEnum jobTypeEnum = postalCodeTimezoneProcessingRequest.getJobType();
    Assertions.assertEquals(JobTypeEnum.UPLOAD_POSTAL_CODE_TIMEZONE, jobTypeEnum);
  }
}
