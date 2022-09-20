package com.hbc.csvdownload.service;

import static org.mockito.Mockito.*;

import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.csvdownload.exception.CsvDataValidationException;
import com.hbc.csvdownload.exception.CsvFormatValidationFailedException;
import com.hbc.csvdownload.exception.CsvParsingException;
import com.hbc.csvdownload.exception.InvalidActionType;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.csvdownload.exception.JsonParsingException;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class CsvUploadUtilityServiceTest {

  @Mock private JobsDashboardClient jobsDashboardClient;

  @InjectMocks private CsvUploadUtilityService csvUploadUtilityService;
  @InjectMocks private TestUtil testUtil;

  @Test
  void uploadProcessingLeadTimesCsv()
      throws IOException, CsvParsingException, CsvFormatValidationFailedException,
          JobSubmissionException, JsonParsingException {
    MultipartFile csvFile = mock(MultipartFile.class);
    String csvFileContent =
        "#CommentedLine1\n"
            + "nodeId,orgId,serviceOptions,processingTime (in hrs),action\n"
            + "1554,BAY,SDND,2,U\n"
            + "1560,BAY,SDND,2,U\n"
            + "1101,BAY,SDND,2,U\n"
            + "1518,BAY,NEXTDAY,6,D\n"
            + "1634,BAY,EXPRESS,30.92,U\n"
            + "1601,BAY,EXPRESS,22.55,U\n"
            + "1125,BAY,EXPRESS,19.90,D\n"
            + "1114,BAY,SDND,24.97,U";
    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    when(jobsDashboardClient.processJobJsonOffline(any(), anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(new JobDto()).build());

    String res = csvUploadUtilityService.uploadProcessingLeadTimesCsv(TestUtil.ORG_ID, csvFile);
    Assertions.assertFalse(ObjectUtils.isEmpty(res));
  }

  @Test
  void uploadProcessingLeadTimesCsvInvalidProcessingLeadTimeException() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);
    String csvFileContent =
        "#CommentedLine1\n"
            + "nodeId,orgId,serviceOptions,processingTime (in hrs),action\n"
            + "1554,BAY,SDND,invalid,U\n"
            + "1560,BAY,SDND,2,U\n"
            + "1101,BAY,SDND,2,U\n"
            + "1518,BAY,NEXTDAY,6,D\n"
            + "1634,BAY,EXPRESS,30.92,U\n"
            + "1601,BAY,EXPRESS,22.55,U\n"
            + "1125,BAY,EXPRESS,19.90,D\n"
            + "1114,BAY,SDND,24.97,U";
    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class,
            () -> csvUploadUtilityService.uploadProcessingLeadTimesCsv(TestUtil.ORG_ID, csvFile));
    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadProcessingLeadTimesCsvInvalidHeaders() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);
    String csvFileContent =
        "nodeId,orgId,serviceOptions\n"
            + "1554,BAY,SDND\n"
            + "1560,BAY,SDND\n"
            + "1101,BAY,SDND\n"
            + "1518,BAY,NEXTDAY\n"
            + "1634,BAY,EXPRESS\n"
            + "1601,BAY,EXPRESS\n"
            + "1125,BAY,EXPRESS\n"
            + "1114,BAY,SDND";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    Exception exception =
        Assertions.assertThrows(
            CsvFormatValidationFailedException.class,
            () -> csvUploadUtilityService.uploadProcessingLeadTimesCsv(TestUtil.ORG_ID, csvFile));
    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadProcessingLeadTimesCsvEmptyCsvFile() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);
    String csvFileContent = "nodeId,orgId,serviceOptions,processingTime (in hrs)";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    Exception exception =
        Assertions.assertThrows(
            CsvFormatValidationFailedException.class,
            () -> csvUploadUtilityService.uploadProcessingLeadTimesCsv(TestUtil.ORG_ID, csvFile));
    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadProcessingLeadTimesCsvEmptyCsvException() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);
    String csvFileContent = "nodeId,orgId,serviceOptions,processingTime (in hrs),action\n";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    Exception exception =
        Assertions.assertThrows(
            CsvFormatValidationFailedException.class,
            () -> csvUploadUtilityService.uploadProcessingLeadTimesCsv(TestUtil.ORG_ID, csvFile));
    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadProcessingLeadTimesCsvEmptyRowException()
      throws IOException, CsvParsingException, CsvFormatValidationFailedException,
          JobSubmissionException, JsonParsingException {
    MultipartFile csvFile = mock(MultipartFile.class);
    String csvFileContent =
        "nodeId,orgId,serviceOptions,processingTime (in hrs),action\n"
            + "1554,BAY,SDND,2,U\n"
            + "1560,BAY,SDND,2,U\n"
            + "1101,BAY,SDND,2,U\n"
            + "1518,BAY,NEXTDAY,6,D\n"
            + "\n"
            + "1601,BAY,EXPRESS,22.55,U\n"
            + "1125,BAY,EXPRESS,19.90,D\n"
            + "1114,BAY,SDND,24.97,U";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    String res = csvUploadUtilityService.uploadProcessingLeadTimesCsv(TestUtil.ORG_ID, csvFile);
    Assertions.assertFalse(ObjectUtils.isEmpty(res));
  }

  @Test
  void uploadProcessingLeadTimesCsvNullAction() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);
    String csvFileContent =
        "nodeId,orgId,serviceOptions,processingTime (in hrs),action\n"
            + "1554,BAY,SDND,2,U\n"
            + "1560,BAY,SDND,2,U\n"
            + "1101,BAY,SDND,2,U\n"
            + "1518,BAY,NEXTDAY,6,D\n"
            + "1634,BAY,EXPRESS,7,\n"
            + "1601,BAY,EXPRESS,22.55,U\n"
            + "1125,BAY,EXPRESS,19.90,D\n"
            + "1114,BAY,SDND,24.97,U";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    Exception exception =
        Assertions.assertThrows(
            InvalidActionType.class,
            () -> csvUploadUtilityService.uploadProcessingLeadTimesCsv(TestUtil.ORG_ID, csvFile));
    Assertions.assertFalse(ObjectUtils.isEmpty(exception));
  }

  @Test
  void uploadProcessingLeadTimesCsvInvalidAction() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);
    String csvFileContent =
        "#CommentedLine1\n"
            + "nodeId,orgId,serviceOptions,processingTime (in hrs),action\n"
            + "1554,BAY,SDND,2,N\n"
            + "1560,BAY,SDND,2,U\n"
            + "1101,BAY,SDND,2,U\n"
            + "1518,BAY,NEXTDAY,6,D\n"
            + "1634,BAY,EXPRESS,7,D\n"
            + "1601,BAY,EXPRESS,22.55,U\n"
            + "1125,BAY,EXPRESS,19.90,D\n"
            + "1114,BAY,SDND,24.97,U";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    Exception exception =
        Assertions.assertThrows(
            InvalidActionType.class,
            () -> csvUploadUtilityService.uploadProcessingLeadTimesCsv(TestUtil.ORG_ID, csvFile));
    Assertions.assertFalse(ObjectUtils.isEmpty(exception));
  }

  @Test
  void uploadProcessingLeadTimesCsvFeignException() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);
    String csvFileContent = TestUtil.processingLeadTimesCsvData;

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    when(jobsDashboardClient.processJobJsonOffline(any(), anyString(), anyString()))
        .thenThrow(
            new FeignException.BadRequest(
                "Failed to create job",
                Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                "Failed to create job".getBytes()));

    Exception exception =
        Assertions.assertThrows(
            JobSubmissionException.class,
            () -> csvUploadUtilityService.uploadProcessingLeadTimesCsv(TestUtil.ORG_ID, csvFile));
    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadProcessingLeadTimesCsvException() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);
    String csvFileContent = TestUtil.processingLeadTimesCsvData;
    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    when(jobsDashboardClient.processJobJsonOffline(any(), anyString(), anyString()))
        .thenThrow(new RuntimeException("Error while submitting job to job framework"));

    Exception exception =
        Assertions.assertThrows(
            JobSubmissionException.class,
            () -> csvUploadUtilityService.uploadProcessingLeadTimesCsv(TestUtil.ORG_ID, csvFile));
    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadTransitTimesCsv()
      throws IOException, CsvFormatValidationFailedException, CsvException, JsonParsingException,
          JobSubmissionException {
    MultipartFile csvFile = mock(MultipartFile.class);

    String csvFileContent =
        "orgId,BAY,,,,,,,,,\n"
            + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
            + "Destination FSA / Source FSA ->,SFSA1,SFSA2,SFSA3\n"
            + "DFSA1,10,9.96,9.96\n"
            + "DFSA2,10,9,9.9\n"
            + "DFSA3,10,9,9\n";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    JobDto jobDto = testUtil.createJob(JobTypeEnum.UPLOAD_TRANSIT_TIMES, 9);

    when(jobsDashboardClient.processJobJsonOffline(any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(jobDto).build());

    String res = csvUploadUtilityService.uploadTransitTimesCsv(TestUtil.ORG_ID, csvFile);

    Assertions.assertFalse(ObjectUtils.isEmpty(res));
    verify(jobsDashboardClient, times(1)).processJobJsonOffline(any(), any(), any());
  }

  @Test
  void uploadTransitTimesCsvNullTransitTime()
      throws IOException, CsvFormatValidationFailedException, JobSubmissionException, CsvException,
          JsonParsingException {
    MultipartFile csvFile = mock(MultipartFile.class);

    JobDto jobDto = testUtil.createJob(JobTypeEnum.UPLOAD_TRANSIT_TIMES, 9);

    String csvFileContent =
        "orgId,BAY,,,,,,,,,\n"
            + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
            + "Destination FSA / Source FSA ->,SFSA1,SFSA2,SFSA3\n"
            + "DFSA1,10,9.96,9.96\n"
            + "DFSA2,10,9,9.9\n"
            + "DFSA3,10,,9\n";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    when(jobsDashboardClient.processJobJsonOffline(any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(jobDto).build());

    String res = csvUploadUtilityService.uploadTransitTimesCsv(TestUtil.ORG_ID, csvFile);

    Assertions.assertFalse(ObjectUtils.isEmpty(res));
    verify(jobsDashboardClient, times(1)).processJobJsonOffline(any(), any(), any());
  }

  @Test
  void uploadTransitTimesCsvInvalidOrgIdHeader() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);

    String csvFileContent =
        "orgIds,BAY,,,,,,,,,\n"
            + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
            + "Destination FSA / Source FSA ->,SFSA1,SFSA2\n"
            + "DFSA1,10,9.96\n"
            + "DFSA2,10,9\n"
            + "DFSA3,10,9\n";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    Exception exception =
        Assertions.assertThrows(
            CsvFormatValidationFailedException.class,
            () -> csvUploadUtilityService.uploadTransitTimesCsv(TestUtil.ORG_ID, csvFile));

    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadTransitTimesCsvInvalidTransitDays() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);

    String csvFileContent =
        "orgId,BAY,,,,,,,,,\n"
            + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
            + "Destination FSA / Source FSA ->,SFSA1,SFSA2,SFSA3\n"
            + "DFSA1,invalid,9.96,9.96\n"
            + "DFSA2,10,9,9.9\n"
            + "DFSA3,10,9,9\n";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class,
            () -> csvUploadUtilityService.uploadTransitTimesCsv(TestUtil.ORG_ID, csvFile));

    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadTransitTimesCsvInvalidCarrierServiceIdHeader() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);

    String csvFileContent =
        "orgId,BAY,,,,,,,,,\n"
            + "Carrier Service,ALL-Standard,,,,,,,,,\n"
            + "Destination FSA / Source FSA ->,SFSA1,SFSA2,SFSA3\n"
            + "DFSA1,10,9.96,9.96\n"
            + "DFSA2,10,9,9.9\n"
            + "DFSA3,10,9,9\n";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    Exception exception =
        Assertions.assertThrows(
            CsvFormatValidationFailedException.class,
            () -> csvUploadUtilityService.uploadTransitTimesCsv(TestUtil.ORG_ID, csvFile));

    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadTransitTimesCsvInvalidFsaHeader() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);

    String csvFileContent =
        "orgId,BAY,,,,,,,,,\n"
            + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
            + "Destination FSA / Source FSA >,SFSA1,SFSA2,SFSA3\n"
            + "DFSA1,10,9.96,9.96\n"
            + "DFSA2,10,9,9.9\n"
            + "DFSA3,10,9,9\n";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    Exception exception =
        Assertions.assertThrows(
            CsvFormatValidationFailedException.class,
            () -> csvUploadUtilityService.uploadTransitTimesCsv(TestUtil.ORG_ID, csvFile));

    Assertions.assertNotNull(exception);
  }

  @Test
  void uploadTransitTimesCsvException() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);

    String csvFileContent =
        "orgId,BAY,,,,,,,,,\n"
            + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
            + "Destination FSA / Source FSA ->,SFSA1,SFSA2,SFSA3\n"
            + "DFSA1,10,9.96,9.96\n"
            + "DFSA2,10,9,9.9\n"
            + "DFSA3,10,9,9\n";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    when(jobsDashboardClient.processJobJsonOffline(any(), any(), any()))
        .thenThrow(new RuntimeException("Error while updating the job"));

    Exception exception =
        Assertions.assertThrows(
            JobSubmissionException.class,
            () -> csvUploadUtilityService.uploadTransitTimesCsv(TestUtil.ORG_ID, csvFile));

    Assertions.assertNotNull(exception);
    verify(jobsDashboardClient, times(1)).processJobJsonOffline(any(), any(), any());
  }

  @Test
  void uploadTransitTimesCsvFeignException() throws IOException {
    MultipartFile csvFile = mock(MultipartFile.class);

    String csvFileContent =
        "orgId,BAY,,,,,,,,,\n"
            + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
            + "Destination FSA / Source FSA ->,SFSA1,SFSA2,SFSA3\n"
            + "DFSA1,10,9.96,9.96\n"
            + "DFSA2,10,9,9.9\n"
            + "DFSA3,10,9,9\n";

    when(csvFile.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()),
            new ByteArrayInputStream(csvFileContent.getBytes()));

    when(jobsDashboardClient.processJobJsonOffline(any(), anyString(), anyString()))
        .thenThrow(
            new FeignException.BadRequest(
                "Failed to create job",
                Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                "Failed to create job".getBytes()));

    Exception exception =
        Assertions.assertThrows(
            JobSubmissionException.class,
            () -> csvUploadUtilityService.uploadTransitTimesCsv(TestUtil.ORG_ID, csvFile));
    Assertions.assertNotNull(exception);
  }
}
