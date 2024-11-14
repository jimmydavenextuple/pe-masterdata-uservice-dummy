package com.nextuple.dataupload.controller;

import static com.nextuple.dataupload.controller.TransferSchedulesController.TRANSFER_SCHEDULE_DEFAULT_SORT_BY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.service.TransferScheduleService;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TransferSchedulesControllerTest {
  @InjectMocks private TransferSchedulesController transferSchedulesController;
  @Mock private TransferScheduleService transferScheduleService;
  @InjectMocks private TestUtil testUtil;

  @Test
  @DisplayName("Get Transfer Schedule List")
  void getTransferSchedulesList() throws CommonServiceException {
    FetchTransferScheduleRequest fetchTransferScheduleRequest =
        FetchTransferScheduleRequest.builder().endDate(new LocalDate()).build();
    when(transferScheduleService.getTransferScheduleList(any(), any(), any()))
        .thenReturn(testUtil.getTransferSchedulePagePayloadResponse());

    ResponseEntity<BaseResponse<PagePayload<TransferScheduleResponse>>> response =
        transferSchedulesController.getTransferSchedulesList(
            TestUtil.ORG_ID,
            1,
            10,
            TRANSFER_SCHEDULE_DEFAULT_SORT_BY,
            "ASC",
            fetchTransferScheduleRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().getPayload().getData().size());

    verify(transferScheduleService, times(1)).getTransferScheduleList(any(), any(), any());
  }
}
