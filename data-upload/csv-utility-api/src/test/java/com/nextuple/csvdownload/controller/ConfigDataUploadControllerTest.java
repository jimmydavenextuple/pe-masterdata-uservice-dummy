package com.nextuple.csvdownload.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.common.outbound.GenericUploadResponse;
import com.nextuple.csvdownload.exception.JobSubmissionException;
import com.nextuple.csvdownload.service.ConfigDataUploadService;
import com.nextuple.csvdownload.util.TestUtil;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;

@ExtendWith(MockitoExtension.class)
class ConfigDataUploadControllerTest {

  @Mock private ConfigDataUploadService configDataUploadService;
  @InjectMocks private ConfigDataUploadController configDataUploadController;
  @InjectMocks private TestUtil testUtil;

  @Test
  void uploadConfigDataTest()
      throws CommonServiceException, JobSubmissionException, IOException, CsvException {
    when(configDataUploadService.processUploadConfigData(any(), any()))
        .thenReturn(testUtil.getGenericUploadResponse());
    ResponseEntity<BaseResponse<GenericUploadResponse>> res =
        configDataUploadController.uploadConfigData("node", testUtil.getGenericUploadRequest());
    Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());
    Assertions.assertNotNull(res);
    Assertions.assertNotNull(res.getBody());
    Assertions.assertFalse(ObjectUtils.isEmpty(res.getBody().getMessage()));
    Assertions.assertEquals(TestUtil.ORG_ID, res.getBody().getPayload().getOrgId());
  }
}
