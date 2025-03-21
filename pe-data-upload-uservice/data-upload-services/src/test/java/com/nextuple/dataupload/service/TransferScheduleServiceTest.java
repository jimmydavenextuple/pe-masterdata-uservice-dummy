package com.nextuple.dataupload.service;

import static com.nextuple.dataupload.controller.TransferSchedulesController.TRANSFER_SCHEDULE_DEFAULT_SORT_BY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.feign.SourcingAttributeFeign;
import com.nextuple.promise.sourcing.rule.api.domain.feign.SourcingAttributesDefinitionFeign;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.GenericPageResponse;
import com.nextuple.transit.domain.feign.TransferScheduleFeign;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import java.util.List;
import java.util.Optional;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Assertions;
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
  @Mock SourcingAttributeFeign sourcingAttributeFeign;
  @Mock SourcingAttributesDefinitionFeign sourcingAttributesDefinitionFeign;

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

  @Test
  @DisplayName("Get Transfer Schedule List for invalid sort order")
  void getTransferScheduleListV2InvalidOrder() throws CommonServiceException {
    try {
      String orgId = "TEST";
      PageParams pageParams = new PageParams();
      pageParams.setSortOrder(Optional.of("ABC"));
      FetchTransferScheduleRequest request = new FetchTransferScheduleRequest();
      Boolean isPagination = true;
      transferScheduleService.getTransferScheduleListV2(orgId, pageParams, request, isPagination);
    } catch (CommonServiceException ce) {
      Assertions.assertEquals(
          "Invalid sort order, consider giving either ASC or DESC", ce.getMessage());
    }
  }

  @Test
  @DisplayName("Get Transfer Schedule List for no sourcing definition found")
  void getTransferScheduleListV2SourcingDefNotFound() throws CommonServiceException {
    String orgId = "TEST";
    PageParams pageParams = new PageParams();
    pageParams.setSortOrder(Optional.of("ASC"));
    FetchTransferScheduleRequest request = getTransferRequest();
    Boolean isPagination = true;
    when(sourcingAttributesDefinitionFeign.getSourcingAttributesDefinitionInActiveStatus(
            orgId, SourcingAttributesDefinitionScopeEnum.TRANSFER_SCHEDULE_RULE))
        .thenReturn(BaseResponse.builder().payload(null).build());
    GenericPageResponse response =
        transferScheduleService.getTransferScheduleListV2(orgId, pageParams, request, isPagination);
    assertNull(response.getData());
    assertNull(response.getPagination());
  }

  @Test
  @DisplayName("Get Transfer Schedule List for no sourcing definition found")
  void getTransferScheduleListV2SourcingDefNotFound() throws CommonServiceException {
    String orgId = "TEST";
    PageParams pageParams = new PageParams();
    pageParams.setSortOrder(Optional.of("ASC"));
    FetchTransferScheduleRequest request = getTransferRequest();
    Boolean isPagination = true;
    when(sourcingAttributesDefinitionFeign.getSourcingAttributesDefinitionInActiveStatus(
            orgId, SourcingAttributesDefinitionScopeEnum.TRANSFER_SCHEDULE_RULE))
        .thenReturn(BaseResponse.builder().payload(null).build());
    GenericPageResponse response =
        transferScheduleService.getTransferScheduleListV2(orgId, pageParams, request, isPagination);
    assertNull(response.getData());
    assertNull(response.getPagination());
  }

  private FetchTransferScheduleRequest getTransferRequest() {
    return FetchTransferScheduleRequest.builder()
        .dropoffNodeIds(List.of("DN01"))
        .sourceNodeIds(List.of("SN01"))
        .startDate(LocalDate.now().minusDays(1))
        .endDate(LocalDate.now())
        .build();
  }
}
