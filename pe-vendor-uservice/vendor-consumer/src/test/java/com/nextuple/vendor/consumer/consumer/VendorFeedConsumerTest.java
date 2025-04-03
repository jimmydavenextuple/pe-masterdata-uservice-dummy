package com.nextuple.vendor.consumer.consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.enums.ActionEnum;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.vendor.consumer.TestUtil;
import com.nextuple.vendor.consumer.dto.VendorFeedDto;
import com.nextuple.vendor.consumer.impl.VendorBatchServiceImpl;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VendorFeedConsumerTest {

  @InjectMocks private VendorFeedConsumer vendorFeedConsumer;
  @Mock private VendorBatchServiceImpl vendorBatchService;
  @InjectMocks private TestUtil testUtil;

  @Test
  void consumeMasterDataFeedTest() {
    List<BatchRequest<VendorFeedDto>> vendorFeedRequests =
        List.of(testUtil.getVendorFeedRequest(ActionEnum.CREATE));
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Vendor created successfully");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getVendorBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    when(vendorBatchService.processRecordsWithRetry(any())).thenReturn(batchResponse);
    vendorFeedConsumer.consumeVendorFeed(vendorFeedRequests, null);

    verify(vendorBatchService, times(1)).processRecordsWithRetry(any());
  }
}
