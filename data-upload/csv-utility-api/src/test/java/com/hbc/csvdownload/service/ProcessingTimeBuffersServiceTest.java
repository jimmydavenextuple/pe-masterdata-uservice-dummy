package com.hbc.csvdownload.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.csvdownload.common.TestUtil;
import com.hbc.dataupload.common.feign.DataUploadFeign;
import com.hbc.dataupload.common.outbound.ProcessingTimeBufferResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ProcessingTimeBuffersServiceTest {

  @InjectMocks private ProcessingTimeBuffersService processingTimeBuffersService;

  @Mock private DataUploadFeign dataUploadFeign;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(processingTimeBuffersService, "pageSize", 1);
  }

  @Test
  void getProcessingTimeBuffersTest() {
    when(dataUploadFeign.getProcessingTimeBufferDetails(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfProcessingTimeBuffers());

    List<ProcessingTimeBufferResponse> responseList =
        processingTimeBuffersService.getProcessingTimeBuffers(TestUtil.ORG_ID);

    assertEquals(4, responseList.size());
    assertEquals(TestUtil.ORG_ID, responseList.get(0).getOrgId());
    assertEquals(TestUtil.NODE_ID, responseList.get(0).getNodeId());
    verify(dataUploadFeign, times(2))
        .getProcessingTimeBufferDetails(any(), any(), any(), any(), any());
  }
}
