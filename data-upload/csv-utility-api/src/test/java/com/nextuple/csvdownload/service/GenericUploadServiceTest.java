package com.nextuple.csvdownload.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.common.outbound.GenericUploadResponse;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.csvdownload.service.v1.ProcessingRequestFactory;
import com.nextuple.csvdownload.service.v1.ProcessingRequestInterface;
import com.nextuple.csvdownload.util.TestUtil;
import com.nextuple.jobs.framework.common.clients.JobsDashboardClient;
import com.nextuple.jobs.framework.common.service.FileService;
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
            "postal-code-timezone", testUtil.getGenericUploadRequest());

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
