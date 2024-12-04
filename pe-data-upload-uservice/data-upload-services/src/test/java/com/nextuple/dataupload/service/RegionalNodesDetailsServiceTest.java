/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.calendar.domain.feign.CalendarFeign;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.common.base.PagePayload;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.domain.dto.NodeListDto;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.node.carrier.domain.feign.impl.NodeCarrierV2Feign;
import com.nextuple.node.domain.feign.NodeFeign;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RegionalNodesDetailsServiceTest {

  @InjectMocks private RegionalNodesDetailsService regionalNodesDetailsService;

  @InjectMocks private TestUtil testUtil;

  @Mock private NodeFeign nodeFeign;
  @Mock private NodeCarrierV2Feign nodeCarrierFeign;
  @Mock private CalendarFeign calendarFeign;
  @Mock private PageProperties pageProperties;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(regionalNodesDetailsService, "nodeCarrierFeign", nodeCarrierFeign);
  }

  @Test
  void getNodeServiceOptionTest1() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    when(calendarFeign.handleGetNodeCalendar(any(), any()))
        .thenReturn((testUtil.getBaseResponseOfNodeCalendarList()));
    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponse());
    when(calendarFeign.getCalendar(any(), any())).thenReturn(testUtil.getBaseResponseOfCalendar());
    when(nodeCarrierFeign.getNodeCarrierList(any(), any()))
        .thenReturn(testUtil.getBaseResponseNodeServiceOptions());
    when(calendarFeign.getNodeCarrierServiceCalendarForOrgIdAndNodeId(any(), any()))
        .thenReturn(testUtil.getNodeCarrierServiceOptionCalendarResponse());

    PageParams pageParams =
        new PageParams(Optional.of(1), Optional.of(1), Optional.of("nodeId"), Optional.of("ASC"));

    PagePayload<NodeListDto> response =
        regionalNodesDetailsService.getNodesList(TestUtil.ORG_ID, null, null, pageParams);

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertEquals(2, response.getData().getFirst().getServiceOptions().size());

    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierListWithLastPickUpTimeDetails(any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierList(any(), any());
    verify(calendarFeign, times(2)).handleGetNodeCalendar(any(), any());
  }

  @Test
  void getNodeServiceOptionTest2() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    BaseResponse<List<NodeCalendarResponse>> nodeCalendarResponse =
        testUtil.getBaseResponseOfNodeCalendarList();
    nodeCalendarResponse.setPayload(Collections.emptyList());
    when(calendarFeign.handleGetNodeCalendar(any(), any())).thenReturn((nodeCalendarResponse));
    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponse());
    when(nodeCarrierFeign.getNodeCarrierList(any(), any()))
        .thenReturn(testUtil.getBaseResponseNodeServiceOptions());
    when(calendarFeign.getNodeCarrierServiceCalendarForOrgIdAndNodeId(any(), any()))
        .thenReturn(testUtil.getNodeCarrierServiceOptionCalendarResponse());
    PageParams pageParams =
        new PageParams(Optional.of(1), Optional.of(1), Optional.of("nodeId"), Optional.of("ASC"));

    PagePayload<NodeListDto> response =
        regionalNodesDetailsService.getNodesList(TestUtil.ORG_ID, null, null, pageParams);

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertNull(response.getData().getFirst().getNodeWorkingCalendar());
    assertEquals(2, response.getData().getFirst().getServiceOptions().size());

    verify(nodeFeign, times(1)).getNodeList(any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierListWithLastPickUpTimeDetails(any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierList(any(), any());
    verify(calendarFeign, times(2)).handleGetNodeCalendar(any(), any());
  }

  @Test
  @DisplayName("Get node list based on nodeIds and nodeType provided")
  void getNodeDetailsTest() {
    when(nodeFeign.getNodeListV2(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    BaseResponse<List<NodeCalendarResponse>> nodeCalendarResponse =
        testUtil.getBaseResponseOfNodeCalendarList();
    nodeCalendarResponse.setPayload(Collections.emptyList());
    when(calendarFeign.handleGetNodeCalendar(any(), any())).thenReturn((nodeCalendarResponse));
    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponse());
    when(nodeCarrierFeign.getNodeCarrierList(any(), any()))
        .thenReturn(testUtil.getBaseResponseNodeServiceOptions());
    when(calendarFeign.getNodeCarrierServiceCalendarForOrgIdAndNodeId(any(), any()))
        .thenReturn(testUtil.getNodeCarrierServiceOptionCalendarResponse());
    PageParams pageParams =
        new PageParams(Optional.of(1), Optional.of(1), Optional.of("nodeId"), Optional.of("ASC"));

    PagePayload<NodeListDto> response =
        regionalNodesDetailsService.getNodesList(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.NODE_TYPE, pageParams);

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertNull(response.getData().getFirst().getNodeWorkingCalendar());
    assertEquals(2, response.getData().getFirst().getServiceOptions().size());
    assertEquals(
        testUtil.getProcessingTimeDetail(),
        response.getData().getFirst().getProcessingTimeDetails());

    verify(nodeFeign, times(1)).getNodeListV2(any(), any(), any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierListWithLastPickUpTimeDetails(any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierList(any(), any());
    verify(calendarFeign, times(2)).handleGetNodeCalendar(any(), any());
  }

  @Test
  @DisplayName("Get node list based on nodeIds provided, nodeType being null")
  void getNodeDetailsValidNodeIds() {
    when(nodeFeign.getNodeListV2(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    BaseResponse<List<NodeCalendarResponse>> nodeCalendarResponse =
        testUtil.getBaseResponseOfNodeCalendarList();
    nodeCalendarResponse.setPayload(Collections.emptyList());
    when(calendarFeign.handleGetNodeCalendar(any(), any())).thenReturn((nodeCalendarResponse));
    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponse());
    when(nodeCarrierFeign.getNodeCarrierList(any(), any()))
        .thenReturn(testUtil.getBaseResponseNodeServiceOptions());
    when(calendarFeign.getNodeCarrierServiceCalendarForOrgIdAndNodeId(any(), any()))
        .thenReturn(testUtil.getNodeCarrierServiceOptionCalendarResponse());
    PageParams pageParams =
        new PageParams(Optional.of(1), Optional.of(1), Optional.of("nodeId"), Optional.of("ASC"));

    PagePayload<NodeListDto> response =
        regionalNodesDetailsService.getNodesList(
            TestUtil.ORG_ID, TestUtil.NODE_ID, null, pageParams);

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertNull(response.getData().getFirst().getNodeWorkingCalendar());
    assertEquals(2, response.getData().getFirst().getServiceOptions().size());
    assertEquals(
        testUtil.getProcessingTimeDetail(),
        response.getData().getFirst().getProcessingTimeDetails());

    verify(nodeFeign, times(1)).getNodeListV2(any(), any(), any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierListWithLastPickUpTimeDetails(any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierList(any(), any());
    verify(calendarFeign, times(2)).handleGetNodeCalendar(any(), any());
  }

  @Test
  @DisplayName("Get node list based on nodeType provided, nodeIds being null")
  void getNodeDetailsValidNodeType() {
    when(nodeFeign.getNodeListV2(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    BaseResponse<List<NodeCalendarResponse>> nodeCalendarResponse =
        testUtil.getBaseResponseOfNodeCalendarList();
    nodeCalendarResponse.setPayload(Collections.emptyList());
    when(calendarFeign.handleGetNodeCalendar(any(), any())).thenReturn((nodeCalendarResponse));
    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponse());
    when(nodeCarrierFeign.getNodeCarrierList(any(), any()))
        .thenReturn(testUtil.getBaseResponseNodeServiceOptions());
    when(calendarFeign.getNodeCarrierServiceCalendarForOrgIdAndNodeId(any(), any()))
        .thenReturn(testUtil.getNodeCarrierServiceOptionCalendarResponse());
    PageParams pageParams =
        new PageParams(Optional.of(1), Optional.of(1), Optional.of("nodeId"), Optional.of("ASC"));

    PagePayload<NodeListDto> response =
        regionalNodesDetailsService.getNodesList(
            TestUtil.ORG_ID, null, TestUtil.NODE_TYPE, pageParams);

    assertEquals(2, response.getPagination().getTotalPages());
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(2, response.getPagination().getTotalRecords());
    assertNotNull(response.getPagination().getNext());
    assertNull(response.getPagination().getPrevious());
    assertNull(response.getData().getFirst().getNodeWorkingCalendar());
    assertEquals(2, response.getData().getFirst().getServiceOptions().size());
    assertEquals(
        testUtil.getProcessingTimeDetail(),
        response.getData().getFirst().getProcessingTimeDetails());

    verify(nodeFeign, times(1)).getNodeListV2(any(), any(), any(), any(), any(), any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierListWithLastPickUpTimeDetails(any(), any());
    verify(nodeCarrierFeign, times(2)).getNodeCarrierList(any(), any());
    verify(calendarFeign, times(2)).handleGetNodeCalendar(any(), any());
  }

  @Test
  @DisplayName("Get node list will all the pickup times details")
  void getNodeListWithPickupTimeDetails() {
    when(nodeFeign.getNodeList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPaginationBaseResponse());
    when(calendarFeign.handleGetNodeCalendar(any(), any()))
        .thenReturn((testUtil.getBaseResponseOfNodeCalendarList()));
    when(nodeCarrierFeign.getNodeCarrierListWithLastPickUpTimeDetails(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfNodeCarrierListResponse());
    when(calendarFeign.getCalendar(any(), any())).thenReturn(testUtil.getBaseResponseOfCalendar());
    when(nodeCarrierFeign.getNodeCarrierList(any(), any()))
        .thenReturn(testUtil.getBaseResponseNodeServiceOptions());
    when(calendarFeign.getNodeCarrierServiceCalendarForOrgIdAndNodeId(any(), any()))
        .thenReturn(testUtil.getNodeCarrierServiceOptionCalendarResponse());
    PageParams pageParams =
        new PageParams(Optional.of(1), Optional.of(1), Optional.of("nodeId"), Optional.of("ASC"));
    PagePayload<NodeListDto> response =
        regionalNodesDetailsService.getNodesList(TestUtil.ORG_ID, null, null, pageParams);
    assertEquals(2, response.getData().getFirst().getPickupTime().size());
    assertEquals(
        TestUtil.CARRIER_SERVICE_ID,
        response.getData().getFirst().getPickupTime().getFirst().getCarrierServiceId());
    assertEquals(
        TestUtil.NODE_ID, response.getData().getFirst().getPickupTime().getFirst().getNodeId());
    assertEquals(
        TestUtil.CALENDAR_ID,
        response.getData().getFirst().getPickupTime().getFirst().getPickupCalendarId());
    assertEquals(
        TestUtil.LAST_PICK_UP_TIME,
        response.getData().getFirst().getPickupTime().getFirst().getPickupTime());
    verify(calendarFeign, times(2)).getNodeCarrierServiceCalendarForOrgIdAndNodeId(any(), any());
  }
}
