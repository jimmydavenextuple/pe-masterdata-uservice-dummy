/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.dataupload.service;

import static com.nextuple.dataupload.controller.TransferSchedulesController.TRANSFER_SCHEDULE_DEFAULT_SORT_BY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.common.outbound.GenericPaginatedTableResponse;
import com.nextuple.dataupload.common.outbound.GenericTableDetails;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.feign.SourcingAttributeFeign;
import com.nextuple.promise.sourcing.rule.api.domain.feign.SourcingAttributesDefinitionFeign;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.transit.domain.feign.TransferScheduleFeign;
import com.nextuple.transit.domain.inbound.FetchTransferScheduleRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
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
    PagePayload.Pagination pagination = new PagePayload.Pagination();
    pagination.setNext("");
    pagination.setPrevious("");
    pagination.setCurrentPage(1);
    pagination.setTotalRecords(0);
    pagination.setSortBy("sourceNodeId");
    pagination.setSortOrder("ASC");
    pagination.setTotalPages(1);
    PagePayload<Object> pagePayload = new PagePayload<>();
    pagePayload.setPagination(pagination);
    pagePayload.setData(List.of());
    when(transferScheduleFeign.fetchTransferSchedule(
            anyString(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString(), any()))
        .thenReturn(BaseResponse.builder().payload(pagePayload).build());
    GenericPaginatedTableResponse response =
        transferScheduleService.getTransferScheduleListV2(orgId, pageParams, request, isPagination);
    assertNotNull(response.getData());
    assertNull(response.getPagination());
  }

  @Test
  @DisplayName("Get Transfer Schedule List V2 with full data")
  void getTransferScheduleListV2WithFullData() throws CommonServiceException {
    // Setup
    String orgId = "TEST";
    PageParams pageParams = new PageParams();
    pageParams.setSortBy(Optional.of("sourceNodeId"));
    pageParams.setSortOrder(Optional.of("ASC"));
    pageParams.setPageNo(Optional.of(1));
    pageParams.setPageSize(Optional.of(10));
    FetchTransferScheduleRequest request = getTransferRequest();
    Boolean isPagination = true;

    SourcingAttributesDefinitionResponse sourcingDefResponse =
        new SourcingAttributesDefinitionResponse();
    sourcingDefResponse.setReqAttributes("1,2");
    sourcingDefResponse.setOptAttributes("3,4");
    when(sourcingAttributesDefinitionFeign.getSourcingAttributesDefinitionInActiveStatus(
            anyString(), any(SourcingAttributesDefinitionScopeEnum.class)))
        .thenReturn(BaseResponse.builder().payload(sourcingDefResponse).build());

    SourcingAttributeResponse reqAttr1 = new SourcingAttributeResponse();
    reqAttr1.setAttributeName("ReqAttr1");
    SourcingAttributeResponse reqAttr2 = new SourcingAttributeResponse();
    reqAttr2.setAttributeName("ReqAttr2");
    when(sourcingAttributeFeign.getSourcingAttributeByOrgIdAndId(orgId, 1L))
        .thenReturn(BaseResponse.builder().payload(reqAttr1).build());
    when(sourcingAttributeFeign.getSourcingAttributeByOrgIdAndId(orgId, 2L))
        .thenReturn(BaseResponse.builder().payload(reqAttr2).build());

    SourcingAttributeResponse optAttr1 = new SourcingAttributeResponse();
    optAttr1.setAttributeName("OptAttr1");
    SourcingAttributeResponse optAttr2 = new SourcingAttributeResponse();
    optAttr2.setAttributeName("OptAttr2");
    when(sourcingAttributeFeign.getSourcingAttributeByOrgIdAndId(orgId, 3L))
        .thenReturn(BaseResponse.builder().payload(optAttr1).build());
    when(sourcingAttributeFeign.getSourcingAttributeByOrgIdAndId(orgId, 4L))
        .thenReturn(BaseResponse.builder().payload(optAttr2).build());

    List<TransferScheduleResponse> transferScheduleList = new ArrayList<>();
    TransferScheduleResponse transferSchedule = prepareTransferScheduleResp();
    transferSchedule.setRuleName("TestRule");
    transferSchedule.setRule("value1:value2:value3:value4");
    transferScheduleList.add(transferSchedule);

    PagePayload<TransferScheduleResponse> pagePayload = new PagePayload<>();
    pagePayload.setData(transferScheduleList);
    PagePayload.Pagination pagination = new PagePayload.Pagination();
    pagination.setTotalRecords(1);
    pagination.setTotalPages(1);
    pagination.setCurrentPage(1);
    pagePayload.setPagination(pagination);

    when(transferScheduleFeign.fetchTransferSchedule(
            anyString(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString(), any()))
        .thenReturn(BaseResponse.builder().payload(pagePayload).build());

    GenericPaginatedTableResponse response =
        transferScheduleService.getTransferScheduleListV2(orgId, pageParams, request, isPagination);

    assertNotNull(response);
    assertNotNull(response.getData());
    assertNotNull(response.getPagination());

    GenericTableDetails detailsResponse = (GenericTableDetails) response.getData();
    assertEquals(9, detailsResponse.getColumns().size());

    List<Map<String, Object>> rows = detailsResponse.getRows();
    assertEquals(1, rows.size());
    Map<String, Object> row = rows.get(0);
    assertEquals("SN01", row.get("sourceNodeId"));
    assertEquals("DN01", row.get("dropoffNodeId"));
    assertEquals("TestRule", row.get("ruleName"));
    assertEquals("value1", row.get("ReqAttr1"));
    assertEquals("value2", row.get("ReqAttr2"));
    assertEquals("value3", row.get("OptAttr1"));
    assertEquals("value4", row.get("OptAttr2"));

    assertEquals(1, response.getPagination().getTotalRecords());
  }

  private static @NotNull TransferScheduleResponse prepareTransferScheduleResp() {
    TransferScheduleResponse transferSchedule = new TransferScheduleResponse();
    transferSchedule.setSourceNodeId("SN01");
    transferSchedule.setDropoffNodeId("DN01");
    transferSchedule.setStartTime(LocalDate.now().minusDays(1).toDate());
    transferSchedule.setEndTime(LocalDate.now().toDate());
    return transferSchedule;
  }

  @Test
  @DisplayName("Get Transfer Schedule List V2 with empty data")
  void getTransferScheduleListV2WithEmptyData() throws CommonServiceException {
    String orgId = "TEST";
    PageParams pageParams = new PageParams();
    pageParams.setSortBy(Optional.of("sourceNodeId"));
    pageParams.setSortOrder(Optional.of("ASC"));
    pageParams.setPageNo(Optional.of(1));
    pageParams.setPageSize(Optional.of(10));
    FetchTransferScheduleRequest request = getTransferRequest();
    Boolean isPagination = true;

    SourcingAttributesDefinitionResponse sourcingDefResponse =
        new SourcingAttributesDefinitionResponse();
    sourcingDefResponse.setReqAttributes("1,2");
    sourcingDefResponse.setOptAttributes("3,4");
    when(sourcingAttributesDefinitionFeign.getSourcingAttributesDefinitionInActiveStatus(
            anyString(), any(SourcingAttributesDefinitionScopeEnum.class)))
        .thenReturn(BaseResponse.builder().payload(sourcingDefResponse).build());

    when(sourcingAttributeFeign.getSourcingAttributeByOrgIdAndId(anyString(), anyLong()))
        .thenReturn(BaseResponse.builder().payload(new SourcingAttributeResponse()).build());

    PagePayload<TransferScheduleResponse> pagePayload = new PagePayload<>();
    pagePayload.setData(Collections.emptyList());
    PagePayload.Pagination pagination = new PagePayload.Pagination();
    pagination.setTotalRecords(0);
    pagination.setTotalPages(0);
    pagination.setCurrentPage(1);
    pagePayload.setPagination(pagination);

    when(transferScheduleFeign.fetchTransferSchedule(
            anyString(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString(), any()))
        .thenReturn(BaseResponse.builder().payload(pagePayload).build());

    GenericPaginatedTableResponse response =
        transferScheduleService.getTransferScheduleListV2(orgId, pageParams, request, isPagination);

    assertNotNull(response);
    assertNotNull(response.getData());
    assertNull(response.getPagination());
  }

  @Test
  @DisplayName("Get Transfer Schedule List V2 with null rule")
  void getTransferScheduleListV2WithNullRule() throws CommonServiceException {
    String orgId = "TEST";
    PageParams pageParams = new PageParams();
    pageParams.setSortBy(Optional.of("sourceNodeId"));
    pageParams.setSortOrder(Optional.of("ASC"));
    pageParams.setPageNo(Optional.of(1));
    pageParams.setPageSize(Optional.of(10));
    FetchTransferScheduleRequest request = getTransferRequest();
    Boolean isPagination = true;

    SourcingAttributesDefinitionResponse sourcingDefResponse =
        new SourcingAttributesDefinitionResponse();
    sourcingDefResponse.setReqAttributes("1");
    sourcingDefResponse.setOptAttributes(null);
    when(sourcingAttributesDefinitionFeign.getSourcingAttributesDefinitionInActiveStatus(
            anyString(), any(SourcingAttributesDefinitionScopeEnum.class)))
        .thenReturn(BaseResponse.builder().payload(sourcingDefResponse).build());

    SourcingAttributeResponse reqAttr = new SourcingAttributeResponse();
    reqAttr.setAttributeName("ReqAttr");
    when(sourcingAttributeFeign.getSourcingAttributeByOrgIdAndId(orgId, 1L))
        .thenReturn(BaseResponse.builder().payload(reqAttr).build());

    List<TransferScheduleResponse> transferScheduleList = new ArrayList<>();
    TransferScheduleResponse transferSchedule = getTransferScheduleResponse();
    transferSchedule.setRuleName(null);
    transferSchedule.setRule(null);
    transferScheduleList.add(transferSchedule);

    PagePayload<TransferScheduleResponse> pagePayload = new PagePayload<>();
    pagePayload.setData(transferScheduleList);
    PagePayload.Pagination pagination = new PagePayload.Pagination();
    pagination.setTotalRecords(1);
    pagination.setTotalPages(1);
    pagination.setCurrentPage(1);
    pagePayload.setPagination(pagination);

    when(transferScheduleFeign.fetchTransferSchedule(
            anyString(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString(), any()))
        .thenReturn(BaseResponse.builder().payload(pagePayload).build());

    GenericPaginatedTableResponse response =
        transferScheduleService.getTransferScheduleListV2(orgId, pageParams, request, isPagination);

    assertNotNull(response);
    assertNotNull(response.getData());
    assertNotNull(response.getPagination());

    GenericTableDetails detailsResponse = (GenericTableDetails) response.getData();
    List<Map<String, Object>> rows = detailsResponse.getRows();
    assertEquals(1, rows.size());
    Map<String, Object> row = rows.get(0);
    assertEquals("SN01", row.get("sourceNodeId"));
    assertEquals("DN01", row.get("dropoffNodeId"));
    assertNull(row.get("ruleName"));
    assertNull(row.get("ReqAttr"));
  }

  private static @NotNull TransferScheduleResponse getTransferScheduleResponse() {
    TransferScheduleResponse transferSchedule = new TransferScheduleResponse();
    transferSchedule.setSourceNodeId("SN01");
    transferSchedule.setDropoffNodeId("DN01");
    transferSchedule.setStartTime(LocalDate.now().minusDays(4).toDate());
    transferSchedule.setEndTime(LocalDate.now().toDate());
    return transferSchedule;
  }

  @Test
  @DisplayName("Get Transfer Schedule List V2 with empty ruleName")
  void getTransferScheduleListV2WithEmptyRuleName() throws CommonServiceException {
    String orgId = "TEST";
    PageParams pageParams = new PageParams();
    pageParams.setSortBy(Optional.of("sourceNodeId"));
    pageParams.setSortOrder(Optional.of("ASC"));
    pageParams.setPageNo(Optional.of(1));
    pageParams.setPageSize(Optional.of(10));
    FetchTransferScheduleRequest request = getTransferRequest();
    Boolean isPagination = true;

    SourcingAttributesDefinitionResponse sourcingDefResponse =
        new SourcingAttributesDefinitionResponse();
    sourcingDefResponse.setReqAttributes("1");
    when(sourcingAttributesDefinitionFeign.getSourcingAttributesDefinitionInActiveStatus(
            anyString(), any(SourcingAttributesDefinitionScopeEnum.class)))
        .thenReturn(BaseResponse.builder().payload(sourcingDefResponse).build());

    SourcingAttributeResponse reqAttr = new SourcingAttributeResponse();
    reqAttr.setAttributeName("ReqAttr");
    when(sourcingAttributeFeign.getSourcingAttributeByOrgIdAndId(orgId, 1L))
        .thenReturn(BaseResponse.builder().payload(reqAttr).build());

    List<TransferScheduleResponse> transferScheduleList = new ArrayList<>();
    TransferScheduleResponse transferSchedule = prepareTransferScheduleResp();
    transferSchedule.setRuleName("");
    transferSchedule.setRule("value1");
    transferScheduleList.add(transferSchedule);

    PagePayload<TransferScheduleResponse> pagePayload = new PagePayload<>();
    pagePayload.setData(transferScheduleList);
    PagePayload.Pagination pagination = new PagePayload.Pagination();
    pagination.setTotalRecords(1);
    pagination.setTotalPages(1);
    pagination.setCurrentPage(1);
    pagePayload.setPagination(pagination);

    when(transferScheduleFeign.fetchTransferSchedule(
            anyString(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString(), any()))
        .thenReturn(BaseResponse.builder().payload(pagePayload).build());

    GenericPaginatedTableResponse response =
        transferScheduleService.getTransferScheduleListV2(orgId, pageParams, request, isPagination);

    assertNotNull(response);
    assertNotNull(response.getData());

    GenericTableDetails detailsResponse = (GenericTableDetails) response.getData();
    List<Map<String, Object>> rows = detailsResponse.getRows();
    assertEquals(1, rows.size());
    Map<String, Object> row = rows.get(0);
    assertEquals("SN01", row.get("sourceNodeId"));
    assertEquals("DN01", row.get("dropoffNodeId"));
    assertNull(row.get("ruleName"));
  }

  @Test
  @DisplayName("Get Transfer Schedule List V2 with fewer rule attributes than expected")
  void getTransferScheduleListV2WithFewerRuleAttributes() throws CommonServiceException {
    String orgId = "TEST";
    PageParams pageParams = new PageParams();
    pageParams.setSortBy(Optional.of("sourceNodeId"));
    pageParams.setSortOrder(Optional.of("ASC"));
    pageParams.setPageNo(Optional.of(1));
    pageParams.setPageSize(Optional.of(10));
    FetchTransferScheduleRequest request = getTransferRequest();
    Boolean isPagination = true;

    SourcingAttributesDefinitionResponse sourcingDefResponse =
        new SourcingAttributesDefinitionResponse();
    sourcingDefResponse.setReqAttributes("1,2");
    sourcingDefResponse.setOptAttributes("3");
    when(sourcingAttributesDefinitionFeign.getSourcingAttributesDefinitionInActiveStatus(
            anyString(), any(SourcingAttributesDefinitionScopeEnum.class)))
        .thenReturn(BaseResponse.builder().payload(sourcingDefResponse).build());

    SourcingAttributeResponse reqAttr1 = new SourcingAttributeResponse();
    reqAttr1.setAttributeName("ReqAttr1");
    SourcingAttributeResponse reqAttr2 = new SourcingAttributeResponse();
    reqAttr2.setAttributeName("ReqAttr2");
    SourcingAttributeResponse optAttr1 = new SourcingAttributeResponse();
    optAttr1.setAttributeName("OptAttr1");

    when(sourcingAttributeFeign.getSourcingAttributeByOrgIdAndId(orgId, 1L))
        .thenReturn(BaseResponse.builder().payload(reqAttr1).build());
    when(sourcingAttributeFeign.getSourcingAttributeByOrgIdAndId(orgId, 2L))
        .thenReturn(BaseResponse.builder().payload(reqAttr2).build());
    when(sourcingAttributeFeign.getSourcingAttributeByOrgIdAndId(orgId, 3L))
        .thenReturn(BaseResponse.builder().payload(optAttr1).build());

    List<TransferScheduleResponse> transferScheduleList = new ArrayList<>();
    TransferScheduleResponse transferSchedule = prepareTransferScheduleResp();
    transferSchedule.setRuleName("TestRule");
    transferSchedule.setRule("value1"); // Only one value when three are expected
    transferScheduleList.add(transferSchedule);

    PagePayload<TransferScheduleResponse> pagePayload = new PagePayload<>();
    pagePayload.setData(transferScheduleList);
    PagePayload.Pagination pagination = new PagePayload.Pagination();
    pagination.setTotalRecords(1);
    pagination.setTotalPages(1);
    pagination.setCurrentPage(1);
    pagePayload.setPagination(pagination);

    when(transferScheduleFeign.fetchTransferSchedule(
            anyString(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString(), any()))
        .thenReturn(BaseResponse.builder().payload(pagePayload).build());

    GenericPaginatedTableResponse response =
        transferScheduleService.getTransferScheduleListV2(orgId, pageParams, request, isPagination);

    assertNotNull(response);
    assertNotNull(response.getData());

    GenericTableDetails detailsResponse = (GenericTableDetails) response.getData();
    List<Map<String, Object>> rows = detailsResponse.getRows();
    assertEquals(1, rows.size());
    Map<String, Object> row = rows.get(0);
    assertEquals("SN01", row.get("sourceNodeId"));
    assertEquals("DN01", row.get("dropoffNodeId"));
    assertEquals("TestRule", row.get("ruleName"));
    assertEquals("value1", row.get("ReqAttr1"));
    assertNull(row.get("ReqAttr2"));
    assertNull(row.get("OptAttr1"));
  }

  @Test
  @DisplayName("Get Transfer Schedule List V2 with null attribute response")
  void getTransferScheduleListV2WithNullAttributeResponse() throws CommonServiceException {
    String orgId = "TEST";
    PageParams pageParams = new PageParams();
    pageParams.setSortBy(Optional.of("sourceNodeId"));
    pageParams.setSortOrder(Optional.of("ASC"));
    pageParams.setPageNo(Optional.of(1));
    pageParams.setPageSize(Optional.of(10));
    FetchTransferScheduleRequest request = getTransferRequest();
    Boolean isPagination = true;

    SourcingAttributesDefinitionResponse sourcingDefResponse =
        new SourcingAttributesDefinitionResponse();
    sourcingDefResponse.setReqAttributes("1,2");
    when(sourcingAttributesDefinitionFeign.getSourcingAttributesDefinitionInActiveStatus(
            anyString(), any(SourcingAttributesDefinitionScopeEnum.class)))
        .thenReturn(BaseResponse.builder().payload(sourcingDefResponse).build());

    SourcingAttributeResponse reqAttr1 = new SourcingAttributeResponse();
    reqAttr1.setAttributeName("ReqAttr1");

    when(sourcingAttributeFeign.getSourcingAttributeByOrgIdAndId(orgId, 1L))
        .thenReturn(BaseResponse.builder().payload(reqAttr1).build());
    when(sourcingAttributeFeign.getSourcingAttributeByOrgIdAndId(orgId, 2L))
        .thenReturn(BaseResponse.builder().payload(null).build());

    List<TransferScheduleResponse> transferScheduleList = new ArrayList<>();
    TransferScheduleResponse transferSchedule = prepareTransferScheduleResp();
    transferSchedule.setRuleName("TestRule");
    transferSchedule.setRule("value1:value2");
    transferScheduleList.add(transferSchedule);

    PagePayload<TransferScheduleResponse> pagePayload = new PagePayload<>();
    pagePayload.setData(transferScheduleList);
    PagePayload.Pagination pagination = new PagePayload.Pagination();
    pagination.setTotalRecords(1);
    pagination.setTotalPages(1);
    pagination.setCurrentPage(1);
    pagePayload.setPagination(pagination);

    when(transferScheduleFeign.fetchTransferSchedule(
            anyString(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString(), any()))
        .thenReturn(BaseResponse.builder().payload(pagePayload).build());

    GenericPaginatedTableResponse response =
        transferScheduleService.getTransferScheduleListV2(orgId, pageParams, request, isPagination);

    assertNotNull(response);
    assertNotNull(response.getData());

    GenericTableDetails detailsResponse = (GenericTableDetails) response.getData();
    List<Map<String, Object>> rows = detailsResponse.getRows();
    assertEquals(1, rows.size());
    Map<String, Object> row = rows.get(0);
    assertEquals("TestRule", row.get("ruleName"));
    assertEquals("value1", row.get("ReqAttr1"));
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
