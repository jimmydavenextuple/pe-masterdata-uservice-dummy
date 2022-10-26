package com.hbc.csvdownload.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.promise.common.domain.SfccOrder;
import com.hbc.promise.common.feign.IntermediaryServiceFeign;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(eddComputationService, "basePath", "");
    ReflectionTestUtils.setField(eddComputationService, "maxSizeInKiloBytes", 10240);
    ReflectionTestUtils.setField(eddComputationService, "maxRows", 1000);
    ReflectionTestUtils.setField(eddComputationService, "maxEddComputationLines", 30);
    ReflectionTestUtils.setField(eddComputationService, "maxEddComputationOrders", 3);
  }

  @Test
  void uploadEddComputationDataTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    File csvContent = eddComputationService.uploadEddCompuationData(absolutePath);
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void uploadEddComputationDataTest2() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "eddComputation", "edd_comp2.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    when(intermediaryServiceFeign.intermediaryCalculateEdd(any(SfccOrder.class)))
        .thenReturn(testUtil.getSfccResponse());
    File csvContent = eddComputationService.uploadEddCompuationData(absolutePath);
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
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
  void downloadEddComputationDataTest7() throws IOException {

    File csvContent =
        eddComputationService.downloadEddComputation(List.of(testUtil.getSfccResponse6()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }
}
