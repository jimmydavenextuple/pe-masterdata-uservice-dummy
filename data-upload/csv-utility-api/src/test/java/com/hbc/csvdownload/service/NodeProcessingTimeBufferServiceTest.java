package com.hbc.csvdownload.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.domain.feign.NodeFeign;
import com.hbc.node.domain.outbound.NodeResponse;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ObjectUtils;

@ExtendWith(MockitoExtension.class)
class NodeProcessingTimeBufferServiceTest {
  @InjectMocks private NodeProcessingTimeBufferService nodeProcessingTimeBufferService;
  @InjectMocks private TestUtil testUtil;
  @Mock private NodeFeign nodeFeign;
  @Mock private NodeCarrierFeign nodeCarrierFeign;

  @Test
  void getProcessingTimeBuffersForOgIdTest() {
    List<NodeResponse> nodeResponseList = testUtil.getNodeResponseList();
    List<NodeCarrierResponse> nodeCarrierResponseList = testUtil.getNodeCarrierResponseList();

    when(nodeFeign.getAllNodesByOrgId(any()))
        .thenReturn(BaseResponse.builder().payload(nodeResponseList).build());
    when(nodeCarrierFeign.getAllNodeCarriersByOrgId(any()))
        .thenReturn(BaseResponse.builder().payload(nodeCarrierResponseList).build());

    String csvRows =
        nodeProcessingTimeBufferService.getProcessingTimeBuffersByOgId(TestUtil.ORG_ID);

    Assertions.assertFalse(ObjectUtils.isEmpty(csvRows));
    Assertions.assertEquals(TestUtil.processingTimeBufferCsvRowData, csvRows);
    verify(nodeFeign, times(1)).getAllNodesByOrgId(any());
    verify(nodeCarrierFeign, times(1)).getAllNodeCarriersByOrgId(any());
  }

  @Test
  void getProcessingTimeBuffersForOgIdNullValuesTest() {
    List<NodeResponse> nodeResponseList = testUtil.getNodeResponseList();
    List<NodeCarrierResponse> nodeCarrierResponseList =
        testUtil.getNodeCarrierResponseListWithNullValues();

    when(nodeFeign.getAllNodesByOrgId(any()))
        .thenReturn(BaseResponse.builder().payload(nodeResponseList).build());
    when(nodeCarrierFeign.getAllNodeCarriersByOrgId(any()))
        .thenReturn(BaseResponse.builder().payload(nodeCarrierResponseList).build());

    String csvRows =
        nodeProcessingTimeBufferService.getProcessingTimeBuffersByOgId(TestUtil.ORG_ID);

    Assertions.assertFalse(ObjectUtils.isEmpty(csvRows));
    Assertions.assertEquals(TestUtil.processingTimeBufferCsvRowDataForNullValues, csvRows);
    verify(nodeFeign, times(1)).getAllNodesByOrgId(any());
    verify(nodeCarrierFeign, times(1)).getAllNodeCarriersByOrgId(any());
  }

  @Test
  void getProcessingTimeBuffersForOgIdPartialNullValuesTest() {
    List<NodeResponse> nodeResponseList = testUtil.getNodeResponseList();
    List<NodeCarrierResponse> nodeCarrierResponseList =
        testUtil.getNodeCarrierResponseListWithPartialNullValues();

    when(nodeFeign.getAllNodesByOrgId(any()))
        .thenReturn(BaseResponse.builder().payload(nodeResponseList).build());
    when(nodeCarrierFeign.getAllNodeCarriersByOrgId(any()))
        .thenReturn(BaseResponse.builder().payload(nodeCarrierResponseList).build());

    String csvRows =
        nodeProcessingTimeBufferService.getProcessingTimeBuffersByOgId(TestUtil.ORG_ID);

    Assertions.assertFalse(ObjectUtils.isEmpty(csvRows));
    Assertions.assertEquals(TestUtil.processingTimeBufferCsvRowDataForPartialNullValues, csvRows);
    verify(nodeFeign, times(1)).getAllNodesByOrgId(any());
    verify(nodeCarrierFeign, times(1)).getAllNodeCarriersByOrgId(any());
  }
}
