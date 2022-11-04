package com.hbc.csvdownload.service;

import static com.hbc.csvdownload.util.TestUtil.FILE_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.csvdownload.common.inbound.GenericUploadRequest;
import com.hbc.jobs.framework.common.domain.outbound.FileResponse;
import com.hbc.jobs.framework.common.service.FileService;
import com.hbc.promise.common.domain.SfccOrder;
import com.hbc.promise.common.feign.IntermediaryServiceFeign;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ObjectUtils;

class EddComputationServiceTest {
  @InjectMocks private EddComputationService eddComputationService;
  @Mock private IntermediaryServiceFeign intermediaryServiceFeign;
  @InjectMocks private TestUtil testUtil;

  @Mock FileService fileService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(eddComputationService, "basePath", "");
    ReflectionTestUtils.setField(eddComputationService, "maxSizeInKiloBytes", 10240);
    ReflectionTestUtils.setField(eddComputationService, "maxRows", 1000);
    ReflectionTestUtils.setField(eddComputationService, "maxEddComputationLines", 14);
    ReflectionTestUtils.setField(eddComputationService, "maxEddComputationOrders", 3);
  }

  @Test
  void uploadEddComputationDataTest() throws CommonServiceException, IOException, CsvException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp2.csv");
    InputStream inputStream = Files.newInputStream(resourceDirectory);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(intermediaryServiceFeign.intermediaryCalculateEdd(any(SfccOrder.class)))
        .thenReturn(testUtil.getSfccResponse());
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse());
    File csvContent =
        eddComputationService.uploadEddCompuationData(
            GenericUploadRequest.builder().filePath(FILE_PATH).build());
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void uploadEddComputationDataTest2() throws CommonServiceException, IOException, CsvException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp2.csv");
    InputStream inputStream = Files.newInputStream(resourceDirectory);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(intermediaryServiceFeign.intermediaryCalculateEdd(any(SfccOrder.class)))
        .thenReturn(testUtil.getSfccResponse());
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse2());
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            eddComputationService.uploadEddCompuationData(
                GenericUploadRequest.builder().filePath(FILE_PATH).build()));
  }

  @Test
  void uploadEddComputationDataTest3() throws CommonServiceException, IOException, CsvException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp2.csv");
    InputStream inputStream = Files.newInputStream(resourceDirectory);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(intermediaryServiceFeign.intermediaryCalculateEdd(any(SfccOrder.class)))
        .thenReturn(testUtil.getSfccResponse());
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse3());
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            eddComputationService.uploadEddCompuationData(
                GenericUploadRequest.builder().filePath(FILE_PATH).build()));
  }

  @Test
  void uploadEddComputationDataTest4() throws CommonServiceException, IOException, CsvException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp2.csv");
    InputStream inputStream = Files.newInputStream(resourceDirectory);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(intermediaryServiceFeign.intermediaryCalculateEdd(any(SfccOrder.class)))
        .thenReturn(testUtil.getSfccResponse());
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse4());
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            eddComputationService.uploadEddCompuationData(
                GenericUploadRequest.builder().filePath(FILE_PATH).build()));
  }

  @Test
  void downloadEddComputationDataTest() throws IOException {
    File csvContent =
        eddComputationService.downloadEddComputation(List.of(testUtil.getSfccResponse()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void downloadEddComputationDataTest2() throws IOException {
    File csvContent =
        eddComputationService.downloadEddComputation(List.of(testUtil.getSfccResponse2()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void downloadEddComputationDataTest3() throws IOException {
    File csvContent =
        eddComputationService.downloadEddComputation(List.of(testUtil.getSfccResponse3()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void downloadEddComputationDataTest4() throws IOException {
    File csvContent =
        eddComputationService.downloadEddComputation(List.of(testUtil.getSfccResponse4()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void downloadEddComputationDataTest5() throws IOException {
    File csvContent =
        eddComputationService.downloadEddComputation(List.of(testUtil.getSfccResponse5()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void downloadEddComputationDataTest6() throws IOException {
    File csvContent =
        eddComputationService.downloadEddComputation(List.of(testUtil.getSfccResponse6()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void uploadEddComputationDataTestForMultipleLineOrder()
      throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp.csv");
    InputStream inputStream = Files.newInputStream(resourceDirectory);
    FileResponse response = testUtil.getFileResponse();
    response.setInputStream(inputStream);
    when(intermediaryServiceFeign.intermediaryCalculateEdd(any(SfccOrder.class)))
        .thenReturn(testUtil.getSfccResponse());
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse());
    File csvContent =
        eddComputationService.uploadEddCompuationData(
            GenericUploadRequest.builder().filePath(FILE_PATH).build());
    String data = FileUtils.readFileToString(csvContent, "UTF-8");
    Assertions.assertEquals(data, testUtil.outPutDataForMultipleLineOrder());
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }
}
