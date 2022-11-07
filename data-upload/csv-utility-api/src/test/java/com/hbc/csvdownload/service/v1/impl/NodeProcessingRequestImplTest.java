package com.hbc.csvdownload.service.v1.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.csvdownload.util.TestUtil;
import com.hbc.jobs.framework.common.clients.FileMetaDataClient;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.FileResponse;
import com.hbc.jobs.framework.common.domain.outbound.PreSignedUrlResponse;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.hbc.jobs.framework.common.service.FileService;
import com.hbc.jobs.framework.common.service.PreSignedUrlInterface;
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
class NodeProcessingRequestImplTest {
  @InjectMocks NodeProcessingRequestImpl nodeProcessingRequest;

  @Spy JobsDashboardClient jobsDashboardClient;

  @InjectMocks private TestUtil testUtil;

  @Mock private FileService fileService;

  @Mock private FileMetaDataClient fileMetaDataClient;
  @Mock private PreSignedUrlInterface preSignedUrlInterface;

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(nodeProcessingRequest, "bucketName", "bucket");
    ReflectionTestUtils.setField(nodeProcessingRequest, "storageType", "s3");
  }

  @Test
  void submitJobTest() throws JobSubmissionException {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_NODES, TestUtil.FILE_METADATA_ID))
        .thenReturn(BaseResponse.builder().payload(testUtil.getJobResponse()).build());
    String result = nodeProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID);
    Assertions.assertEquals(TestUtil.JOB_ID, result);
  }

  @Test
  void submitJobFeignExceptionTest() {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_NODES, TestUtil.FILE_METADATA_ID))
        .thenThrow(FeignException.class);
    Assertions.assertThrows(
        JobSubmissionException.class,
        () -> nodeProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID));
  }

  @Test
  void submitJobExceptionTest() {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID, JobTypeEnum.UPLOAD_NODES, TestUtil.FILE_METADATA_ID))
        .thenThrow(ArithmeticException.class);
    Assertions.assertThrows(
        JobSubmissionException.class,
        () -> nodeProcessingRequest.submitJob(TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID));
  }

  @Test
  void validateCsvTest() throws IOException, CommonServiceException, CsvException {
    Path path = Paths.get("src", "test", "resources", "node", "node.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    nodeProcessingRequest.validate(testUtil.getGenericUploadRequest(), response);
  }

  @Test
  void validateInvalidHeadersExceptionTest() throws IOException {
    Path path = Paths.get("src", "test", "resources", "node", "nodeInvalidHeader.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () -> nodeProcessingRequest.validate(testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals("Node data uploaded file has invalid headers.", ex.getMessage());
  }

  @Test
  void validateEmptyCsvExceptionTest() throws IOException {
    Path path = Paths.get("src", "test", "resources", "node", "nodeEmpty.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () -> nodeProcessingRequest.validate(testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals("No Records found in the csv", ex.getMessage());
  }

  @Test
  void validateInvalidFileTypeExceptionTest() {
    FileResponse response = testUtil.getFileResponse();
    response.setContentType("");
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () -> nodeProcessingRequest.validate(testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals("Node data uploaded file has invalid file type.", ex.getMessage());
  }

  @Test
  void downloadErrorLogsTest1() throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto = testUtil.createJob(TestUtil.JOB_ID, JobTypeEnum.UPLOAD_NODES, 5);
    RecordStatusDto recordStatusDto = testUtil.getJobRecordsForNodes();
    when(jobsDashboardClient.getJobRecordsByFilters(
            anyString(), anyString(), anyString(), any(), any()))
        .thenReturn(
            BaseResponse.builder()
                .payload(
                    testUtil.createPagePayloadRecordStatusDto(List.of(recordStatusDto), 2, 5, 1))
                .build());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());
    when(preSignedUrlInterface.downloadFileURLById(1L))
        .thenReturn(testUtil.getPreSignedUrlResponse());
    when(fileMetaDataClient.createFileMetadata(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getFileMetaDataResponse()).build());
    PreSignedUrlResponse preSignedUrlResponse =
        nodeProcessingRequest.downloadErrorLogs(jobDto, Optional.of(ApiStatusEnum.FAILURE.name()));
    Assertions.assertNotNull(preSignedUrlResponse);
    Assertions.assertFalse(ObjectUtils.isEmpty(preSignedUrlResponse));
  }

  @Test
  void downloadErrorLogsTest2() throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto = testUtil.createJob(TestUtil.JOB_ID, JobTypeEnum.UPLOAD_NODES, 5);
    RecordStatusDto recordStatusDto = testUtil.getJobRecordsForNodes1();
    when(jobsDashboardClient.getJobRecordsByFilters(
            anyString(), anyString(), anyString(), any(), any()))
        .thenReturn(
            BaseResponse.builder()
                .payload(
                    testUtil.createPagePayloadRecordStatusDto(List.of(recordStatusDto), 2, 5, 1))
                .build());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());
    when(preSignedUrlInterface.downloadFileURLById(1L))
        .thenReturn(testUtil.getPreSignedUrlResponse());
    when(fileMetaDataClient.createFileMetadata(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getFileMetaDataResponse()).build());
    PreSignedUrlResponse preSignedUrlResponse =
        nodeProcessingRequest.downloadErrorLogs(jobDto, Optional.of(ApiStatusEnum.FAILURE.name()));
    Assertions.assertNotNull(preSignedUrlResponse);
    Assertions.assertFalse(ObjectUtils.isEmpty(preSignedUrlResponse));
  }

  @Test
  void getJobType() {
    JobTypeEnum jobTypeEnum = nodeProcessingRequest.getJobType();
    Assertions.assertEquals(JobTypeEnum.UPLOAD_NODES, jobTypeEnum);
  }
}
