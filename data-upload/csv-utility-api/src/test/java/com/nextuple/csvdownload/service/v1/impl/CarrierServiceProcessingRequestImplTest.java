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
class CarrierServiceProcessingRequestImplTest {
  @InjectMocks CarrierServiceProcessingRequestImpl carrierServiceProcessingRequest;
  @Mock private FileService fileService;
  @Mock private PreSignedUrlInterface preSignedUrlInterface;
  @Mock private FileMetaDataClient fileMetaDataClient;
  @InjectMocks com.nextuple.csvdownload.common.TestUtil testUtil1;
  @Spy JobsDashboardClient jobsDashboardClient;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(carrierServiceProcessingRequest, "bucketName", "bucket");
    ReflectionTestUtils.setField(carrierServiceProcessingRequest, "storageType", "s3");
  }

  @Test
  void submitJobTest() throws JobSubmissionException {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_CARRIER_SERVICE, TestUtil.FILE_METADATA_ID))
        .thenReturn(BaseResponse.builder().payload(testUtil.getJobResponse()).build());
    String result =
        carrierServiceProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID);
    Assertions.assertEquals(TestUtil.JOB_ID, result);
  }

  @Test
  void submitJobFeignExceptionTest() {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_CARRIER_SERVICE, TestUtil.FILE_METADATA_ID))
        .thenThrow(FeignException.class);
    Assertions.assertThrows(
        JobSubmissionException.class,
        () ->
            carrierServiceProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID));
  }

  @Test
  void submitJobExceptionTest() {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_CARRIER_SERVICE, TestUtil.FILE_METADATA_ID))
        .thenThrow(ArithmeticException.class);
    Assertions.assertThrows(
        JobSubmissionException.class,
        () ->
            carrierServiceProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID));
  }

  @Test
  void validateCsvTest() throws IOException, CommonServiceException, CsvException {
    Path path = Paths.get("src", "test", "resources", "carrierService", "carrierService.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    carrierServiceProcessingRequest.validate(testUtil.getGenericUploadRequest(), response);
  }

  @Test
  void validateInvalidHeadersExceptionTest() throws IOException {
    Path path =
        Paths.get("src", "test", "resources", "carrierService", "carrierServiceInvalidHeader.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                carrierServiceProcessingRequest.validate(
                    testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals(
        "Carrier Service data uploaded file has invalid headers.", ex.getMessage());
  }

  @Test
  void validateEmptyCsvExceptionTest() throws IOException {
    Path path = Paths.get("src", "test", "resources", "carrierService", "carrierServiceEmpty.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                carrierServiceProcessingRequest.validate(
                    testUtil.getGenericUploadRequest(), response));
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
                carrierServiceProcessingRequest.validate(
                    testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals(
        "Carrier Service data uploaded file has invalid file type.", ex.getMessage());
  }

  @Test
  void downloadErrorLogs() throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto = testUtil.createJob(TestUtil.JOB_ID, JobTypeEnum.UPLOAD_CARRIER_SERVICE, 20);
    RecordStatusDto recordStatusDto = testUtil1.getJobRecordsForCarrierService();
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
    when(fileMetaDataClient.createFileMetadata(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getFileMetaDataResponse()).build());

    PreSignedUrlResponse preSignedUrlResponse =
        carrierServiceProcessingRequest.downloadErrorLogs(
            jobDto, Optional.of(ApiStatusEnum.FAILURE.name()));

    Assertions.assertNotNull(preSignedUrlResponse);
    Assertions.assertFalse(ObjectUtils.isEmpty(preSignedUrlResponse));
  }

  @Test
  void getJobType() {
    JobTypeEnum jobTypeEnum = carrierServiceProcessingRequest.getJobType();
    Assertions.assertEquals(JobTypeEnum.UPLOAD_CARRIER_SERVICE, jobTypeEnum);
  }
}
