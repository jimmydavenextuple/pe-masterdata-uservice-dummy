package com.hbc.csvdownload.service.v1.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
import net.logstash.logback.encoder.org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NodeServiceOptionBufferProcessingRequestImplTest {
  @InjectMocks
  NodeServiceOptionBufferProcessingRequestImpl nodeServiceOptionBufferProcessingRequest;

  @Spy JobsDashboardClient jobsDashboardClient;

  @Mock FileService fileService;

  @Mock PreSignedUrlInterface preSignedUrlInterface;

  @Mock FileMetaDataClient fileMetaDataClient;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(nodeServiceOptionBufferProcessingRequest, "bucketName", "bucket");
    ReflectionTestUtils.setField(nodeServiceOptionBufferProcessingRequest, "storageType", "s3");
  }

  @Test
  void submitJobTest() throws JobSubmissionException {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID,
            JobTypeEnum.UPLOAD_NODE_SERVICE_OPTION_BUFFER,
            TestUtil.FILE_METADATA_ID))
        .thenReturn(BaseResponse.builder().payload(testUtil.getJobResponse()).build());
    String result =
        nodeServiceOptionBufferProcessingRequest.submitJob(
            TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID);
    Assertions.assertEquals(TestUtil.JOB_ID, result);
  }

  @Test
  void submitJobFeignExceptionTest() {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID,
            JobTypeEnum.UPLOAD_NODE_SERVICE_OPTION_BUFFER,
            TestUtil.FILE_METADATA_ID))
        .thenThrow(FeignException.class);
    Assertions.assertThrows(
        JobSubmissionException.class,
        () ->
            nodeServiceOptionBufferProcessingRequest.submitJob(
                TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID));
  }

  @Test
  void submitJobExceptionTest() {
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(
            TestUtil.ORG_ID,
            JobTypeEnum.UPLOAD_NODE_SERVICE_OPTION_BUFFER,
            TestUtil.FILE_METADATA_ID))
        .thenThrow(ArithmeticException.class);
    Assertions.assertThrows(
        JobSubmissionException.class,
        () ->
            nodeServiceOptionBufferProcessingRequest.submitJob(
                TestUtil.ORG_ID, TestUtil.FILE_METADATA_ID));
  }

  @Test
  void validateCsvTest() throws IOException, CommonServiceException, CsvException {
    Path path =
        Paths.get(
            "src", "test", "resources", "nodeServiceOptionBuffer", "nodeServiceOptionBuffer.csv");
    InputStream inputStream = Files.newInputStream(path);
    FileResponse response = testUtil.getFileResponse();
    response.setFileName("nodeServiceOptionBuffer.csv");
    response.setInputStream(inputStream);
    nodeServiceOptionBufferProcessingRequest.validate(testUtil.getGenericUploadRequest(), response);
    Assertions.assertEquals("nodeServiceOptionBuffer.csv", response.getFileName());
  }

  @Test
  void validateInvalidHeadersExceptionTest() throws IOException {
    Path path =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeServiceOptionBuffer",
            "nodeServiceOptionBufferInvalidHeaders.csv");
    FileResponse response = getFileResponseWithInputStream(path);
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeServiceOptionBufferProcessingRequest.validate(
                    testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals(
        "Node service option buffer data uploaded file has invalid headers.", ex.getMessage());
  }

  @Test
  void validateEmptyCsvExceptionTest() throws IOException {
    Path path =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeServiceOptionBuffer",
            "nodeServiceOptionBufferEmpty.csv");
    FileResponse response = getFileResponseWithInputStream(path);
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeServiceOptionBufferProcessingRequest.validate(
                    testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals("No Records found in the csv", ex.getMessage());
  }

  @Test
  void validateFullEmptyCsvExceptionTest() throws IOException {
    Path path =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeServiceOptionBuffer",
            "nodeServiceOptionBufferFullEmpty.csv");
    FileResponse fileResponse = getFileResponseWithInputStream(path);
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                nodeServiceOptionBufferProcessingRequest.validate(
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
                nodeServiceOptionBufferProcessingRequest.validate(
                    testUtil.getGenericUploadRequest(), response));
    Assertions.assertEquals(
        "Node service option buffer data uploaded file has invalid file type.", ex.getMessage());
  }

  @Test
  void downloadLogsTest() throws IOException, CommonServiceException, JobSubmissionException {
    JobDto jobDto =
        testUtil.createJobForDownloadLogs(TestUtil.JOB_ID, JobTypeEnum.UPLOAD_CALENDER, 20);
    RecordStatusDto recordStatusDto = testUtil.getRecordStatusDtoForNodeServiceOptionBuffer();
    when(jobsDashboardClient.getJobRecordsByFilters(
            anyString(), anyString(), anyString(), anyInt(), anyInt()))
        .thenReturn(
            BaseResponse.builder()
                .payload(
                    testUtil.createPagePayloadRecordStatusDto(List.of(recordStatusDto), 5, 20, 1))
                .build());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());
    when(preSignedUrlInterface.downloadFileURLById(1L))
        .thenReturn(testUtil.getPreSignedUrlResponseForNodeServiceOptionBuffer());
    when(fileMetaDataClient.createFileMetadata(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getFileMetaDataResponse()).build());

    PreSignedUrlResponse preSignedUrlResponse =
        nodeServiceOptionBufferProcessingRequest.downloadErrorLogs(
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
    JobTypeEnum jobTypeEnum = nodeServiceOptionBufferProcessingRequest.getJobType();
    Assertions.assertEquals(JobTypeEnum.UPLOAD_NODE_SERVICE_OPTION_BUFFER, jobTypeEnum);
  }
}
