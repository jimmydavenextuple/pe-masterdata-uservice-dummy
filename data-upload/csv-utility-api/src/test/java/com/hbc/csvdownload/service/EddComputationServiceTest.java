package com.hbc.csvdownload.service;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.dataupload.common.utils.DataUploadUtil;
import com.hbc.intermediary.feign.IntermediaryServiceFeign;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ObjectUtils;

@ExtendWith(MockitoExtension.class)
class EddComputationServiceTest {

  @Mock private IntermediaryServiceFeign intermediaryServiceFeign;
  @Mock DataUploadUtil dataUploadUtil;
  @Mock Paths paths;
  @InjectMocks private EddComputationService eddComputationService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(eddComputationService, "basePath", "");
    ReflectionTestUtils.setField(eddComputationService, "maxSizeInKiloBytes", 10240);
    ReflectionTestUtils.setField(eddComputationService, "maxRows", 1000);
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

}
