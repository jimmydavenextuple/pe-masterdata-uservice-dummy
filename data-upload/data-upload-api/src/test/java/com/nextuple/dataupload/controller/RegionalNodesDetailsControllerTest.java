package com.nextuple.dataupload.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.domain.dto.NodeListDto;
import com.nextuple.dataupload.service.RegionalNodesDetailsService;
import com.nextuple.dataupload.util.TestUtil;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class RegionalNodesDetailsControllerTest {

  @InjectMocks private RegionalNodesDetailsController regionalNodesDetailsController;

  @Mock private RegionalNodesDetailsService regionalNodesDetailsService;

  @InjectMocks private TestUtil testUtil;

  @Mock private PageProperties pageProperties;

  @Test
  void getNodesListTest() {
    when(regionalNodesDetailsService.getNodesList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPagePayload(2));

    ResponseEntity<BaseResponse<PagePayload<NodeListDto>>> response =
        regionalNodesDetailsController.getNodesList(
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.of(2), Optional.of(1), Optional.of("nodeId"), Optional.of("DESC")));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().getPayload().getData().size());
    assertEquals(4, response.getBody().getPayload().getPagination().getTotalRecords());
    assertEquals(2, response.getBody().getPayload().getPagination().getCurrentPage());
    assertEquals(2, response.getBody().getPayload().getPagination().getTotalPages());
    assertNotNull(response.getBody().getPayload().getPagination().getPrevious());
    assertEquals("", response.getBody().getPayload().getPagination().getNext());

    verify(regionalNodesDetailsService, times(1)).getNodesList(any(), any(), any(), any(), any());
  }

  @Test
  void getNodesListDefaultsTest() {
    when(pageProperties.getPageNo()).thenReturn(1);
    when(pageProperties.getPageSize()).thenReturn(15);
    when(regionalNodesDetailsService.getNodesList(any(), any(), any(), any(), any()))
        .thenReturn(testUtil.getNodeListPagePayload(1));

    ResponseEntity<BaseResponse<PagePayload<NodeListDto>>> response =
        regionalNodesDetailsController.getNodesList(
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().getPayload().getData().size());
    assertEquals(4, response.getBody().getPayload().getPagination().getTotalRecords());
    assertEquals(1, response.getBody().getPayload().getPagination().getCurrentPage());
    assertEquals(2, response.getBody().getPayload().getPagination().getTotalPages());
    assertNotNull(response.getBody().getPayload().getPagination().getNext());
    assertEquals("", response.getBody().getPayload().getPagination().getPrevious());

    verify(regionalNodesDetailsService, times(1)).getNodesList(any(), any(), any(), any(), any());
  }
}
