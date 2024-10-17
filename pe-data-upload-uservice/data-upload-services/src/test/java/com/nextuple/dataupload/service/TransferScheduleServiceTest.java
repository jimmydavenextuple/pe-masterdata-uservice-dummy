package com.nextuple.dataupload.service;

import static com.nextuple.dataupload.controller.TransferSchedulesController.TRANSFER_SCHEDULE_DEFAULT_SORT_BY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.transit.domain.feign.TransferScheduleFeign;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import java.util.Optional;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransferScheduleServiceTest {
  @InjectMocks private TransferScheduleService transferScheduleService;
  @InjectMocks private TestUtil testUtil;
  @Mock private TransferScheduleFeign transferScheduleFeign;

  @Test
  @DisplayName("Get Transfer Schedule List")
  void getTransferScheduleList() {
    var baseResponse =
        BaseResponse.builder().payload(testUtil.getTransferSchedulePagePayloadResponse()).build();
    var request = FetchTransferScheduleRequest.builder().endDate(new LocalDate()).build();
    when(transferScheduleFeign.fetchTransferSchedule(
            any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(baseResponse);

    var response =
        transferScheduleService.getTransferScheduleList(
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.of(2),
                Optional.of(1),
                Optional.of(TRANSFER_SCHEDULE_DEFAULT_SORT_BY),
                Optional.of("DESC")),
            request);

    assertEquals(response.getData().getFirst().getDropoffNodeId(), "Node-1");
    assertEquals(response.getData().getFirst().getSourceNodeId(), "Node-2");

    verify(transferScheduleFeign, times(1))
        .fetchTransferSchedule(any(), any(), any(), any(), any(), any(), any());
  }
}
