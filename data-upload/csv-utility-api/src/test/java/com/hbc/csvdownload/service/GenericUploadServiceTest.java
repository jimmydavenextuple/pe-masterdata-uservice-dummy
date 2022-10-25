package com.hbc.csvdownload.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.outbound.GenericUploadResponse;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.csvdownload.service.v1.ProcessingRequestFactory;
import com.hbc.csvdownload.service.v1.ProcessingRequestInterface;
import com.hbc.csvdownload.util.TestUtil;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.service.FileService;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GenericUploadServiceTest {

  @Mock private FileService fileService;
  @Mock private JobsDashboardClient jobsDashboardClient;

  @Mock ProcessingRequestFactory processingRequestFactory;
  @InjectMocks private ConfigDataUploadService configDataUploadService;

  @InjectMocks private TestUtil testUtil;

  @Test
  void getCarrierServiceCalenderTest()
      throws CommonServiceException, JobSubmissionException, IOException, CsvException {
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse());
    when(jobsDashboardClient.createFileMetadata(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getFileMetaDataResponse()).build());
    when(processingRequestFactory.getModule(any()))
        .thenReturn(mock(ProcessingRequestInterface.class));

    GenericUploadResponse response =
        configDataUploadService.processUploadConfigData(
            "market-region", testUtil.getGenericUploadRequest());

    Assertions.assertEquals(TestUtil.ORG_ID, response.getOrgId());
    verify(fileService, times(1)).getFile(any(), any());
    verify(jobsDashboardClient, times(1)).createFileMetadata(any());
  }

  @Test
  void getCarrierServiceCalenderExceptionTest() throws CommonServiceException {
    Exception ex =
        Assertions.assertThrows(
            Exception.class,
            () ->
                configDataUploadService.processUploadConfigData(
                    "market-region1", testUtil.getGenericUploadRequest()));

    Assertions.assertEquals("Invalid module name supplied", ex.getMessage());
    verify(fileService, times(0)).getFile(any(), any());
    verify(jobsDashboardClient, times(0)).createFileMetadata(any());
  }
}
