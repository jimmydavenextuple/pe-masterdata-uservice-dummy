/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.feign.HolidayCutoffUIFeign;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.HolidayCutoffDetailsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponseForHolidayCutoff;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PaginationAttributeForHolidayCutoff;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class HolidayCutoffDetailsServiceTest {

  @InjectMocks HolidayCutoffDetailsService holidayCutoffDetailsService;

  @Mock HolidayCutoffUIFeign holidayCutoffUIFeign;

  @BeforeEach
  public void init() {
    ReflectionTestUtils.setField(holidayCutoffDetailsService, "sortOrder", "ASC");
    ReflectionTestUtils.setField(holidayCutoffDetailsService, "sortBy", "ruleName");
  }

  @Test
  @DisplayName("Get Holiday cutoff details by calling holidayCutoffUIFeign")
  void getHolidayCutoffDetailsTest() {
    PageParams pageParams = new PageParams();
    Boolean isPaginated = true;

    PageResponseForHolidayCutoff feignResponse = new PageResponseForHolidayCutoff();

    ResponseEntity<BaseResponse<PageResponseForHolidayCutoff>> responseEntity =
        ResponseEntity.ok(BaseResponse.builder().payload(feignResponse).build());

    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();

    when(holidayCutoffUIFeign.getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            anyString(),
            anyString(),
            any(HolidayCutoffUIRequest.class)))
        .thenReturn(responseEntity);

    PageResponseForHolidayCutoff response =
        holidayCutoffDetailsService.getHolidayCutoffDetails(
            TestUtil.ORG_ID, pageParams, isPaginated, holidayCutoffUIRequest);

    assertNotNull(response);

    verify(holidayCutoffUIFeign, times(1))
        .getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            anyString(),
            anyString(),
            any(HolidayCutoffUIRequest.class));
  }

  @Test
  @DisplayName("Get Holiday cutoff details by calling holidayCutoffUIFeign with pageNumber")
  void getHolidayCutoffDetailsTestWithPageNo() {
    PageParams pageParams = new PageParams();
    pageParams.setPageNo(Optional.of(2));
    Boolean isPaginated = true;

    PageResponseForHolidayCutoff feignResponse = new PageResponseForHolidayCutoff();
    PaginationAttributeForHolidayCutoff pagination = new PaginationAttributeForHolidayCutoff();
    pagination.setCurrentPage(2);
    feignResponse.setPagination(pagination);

    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();

    ResponseEntity<BaseResponse<PageResponseForHolidayCutoff>> responseEntity =
        ResponseEntity.ok(BaseResponse.builder().payload(feignResponse).build());
    when(holidayCutoffUIFeign.getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            anyString(),
            anyString(),
            any(HolidayCutoffUIRequest.class)))
        .thenReturn(responseEntity);
    PageResponseForHolidayCutoff response =
        holidayCutoffDetailsService.getHolidayCutoffDetails(
            TestUtil.ORG_ID, pageParams, isPaginated, holidayCutoffUIRequest);
    assertNotNull(response);
    verify(holidayCutoffUIFeign, times(1))
        .getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            anyString(),
            anyString(),
            any(HolidayCutoffUIRequest.class));
    assertEquals(2, response.getPagination().getCurrentPage());
  }

  @Test
  @DisplayName("Get Holiday cutoff details when holidayCutoffDetailsResponseEntity is null")
  void getHolidayCutoffDetailsWhenEntityIsNullTest() {
    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    PageParams pageParams = new PageParams();
    Boolean isPaginated = true;

    when(holidayCutoffUIFeign.getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            anyString(),
            anyString(),
            any(HolidayCutoffUIRequest.class)))
        .thenReturn(null);

    PageResponseForHolidayCutoff response =
        holidayCutoffDetailsService.getHolidayCutoffDetails(
            TestUtil.ORG_ID, pageParams, isPaginated, holidayCutoffUIRequest);

    assertNull(response);
  }

  @Test
  @DisplayName("Get Holiday cutoff details when holidayCutoffDetailsResponseEntity body is null")
  void getHolidayCutoffDetailsWhenBodyIsNullTest() {
    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    PageParams pageParams = new PageParams();
    Boolean isPaginated = true;

    ResponseEntity<BaseResponse<PageResponseForHolidayCutoff>> responseEntity =
        ResponseEntity.ok(BaseResponse.builder().build());

    when(holidayCutoffUIFeign.getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            anyString(),
            anyString(),
            any(HolidayCutoffUIRequest.class)))
        .thenReturn(responseEntity);

    PageResponseForHolidayCutoff response =
        holidayCutoffDetailsService.getHolidayCutoffDetails(
            TestUtil.ORG_ID, pageParams, isPaginated, holidayCutoffUIRequest);

    assertNull(response);
  }

  @Test
  @DisplayName(
      "Get Holiday cutoff details when holidayCutoffDetailsResponseEntity body payload is null")
  void getHolidayCutoffDetailsWhenPayloadIsNullTest() {
    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    PageParams pageParams = new PageParams();
    Boolean isPaginated = true;

    ResponseEntity<BaseResponse<PageResponseForHolidayCutoff>> responseEntity =
        ResponseEntity.ok(BaseResponse.builder().payload(null).build());

    when(holidayCutoffUIFeign.getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            anyString(),
            anyString(),
            any(HolidayCutoffUIRequest.class)))
        .thenReturn(responseEntity);

    PageResponseForHolidayCutoff response =
        holidayCutoffDetailsService.getHolidayCutoffDetails(
            TestUtil.ORG_ID, pageParams, isPaginated, holidayCutoffUIRequest);

    assertNull(response);
  }

  @Test
  @DisplayName("Get Holiday cutoff details when holidayCutoffUIFeign throws an exception")
  void getHolidayCutoffDetailsWhenFeignThrowsExceptionTest() {
    PageParams pageParams = new PageParams();
    Boolean isPaginated = true;

    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();

    when(holidayCutoffUIFeign.getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            anyString(),
            anyString(),
            any(HolidayCutoffUIRequest.class)))
        .thenThrow(new RuntimeException("Feign client exception"));

    PageResponseForHolidayCutoff response =
        holidayCutoffDetailsService.getHolidayCutoffDetails(
            TestUtil.ORG_ID, pageParams, isPaginated, holidayCutoffUIRequest);

    assertNull(response);

    verify(holidayCutoffUIFeign, times(1))
        .getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            anyString(),
            anyString(),
            any(HolidayCutoffUIRequest.class));
  }

  @Test
  @DisplayName("Get Holiday cutoff details when responseBody is null")
  void getHolidayCutoffDetailsWhenResponseBodyIsNullTest() {
    PageParams pageParams = new PageParams();
    Boolean isPaginated = true;

    ResponseEntity<BaseResponse<PageResponseForHolidayCutoff>> responseEntity =
        ResponseEntity.ok().body(null);

    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();

    when(holidayCutoffUIFeign.getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            anyString(),
            anyString(),
            any(HolidayCutoffUIRequest.class)))
        .thenReturn(responseEntity);

    PageResponseForHolidayCutoff response =
        holidayCutoffDetailsService.getHolidayCutoffDetails(
            TestUtil.ORG_ID, pageParams, isPaginated, holidayCutoffUIRequest);

    assertNull(response);

    verify(holidayCutoffUIFeign, times(1))
        .getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            anyString(),
            anyString(),
            any(HolidayCutoffUIRequest.class));
  }

  @Test
  @DisplayName("Get Holiday cutoff details when response is null")
  void getHolidayCutoffDetailsWhenResponseIsNullTest() {
    PageParams pageParams = new PageParams();
    Boolean isPaginated = true;

    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();

    when(holidayCutoffUIFeign.getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            anyString(),
            anyString(),
            any(HolidayCutoffUIRequest.class)))
        .thenReturn(null);
    PageResponseForHolidayCutoff response =
        holidayCutoffDetailsService.getHolidayCutoffDetails(
            TestUtil.ORG_ID, pageParams, isPaginated, holidayCutoffUIRequest);
    assertNull(response);
    verify(holidayCutoffUIFeign, times(1))
        .getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            anyString(),
            anyString(),
            any(HolidayCutoffUIRequest.class));
  }

  @Test
  @DisplayName("Get Holiday cutoff details when holidayCutoffDetailsResponseEntity is not null")
  void getHolidayCutoffDetailsWhenEntityIsNotNullTest() {
    PageParams pageParams = new PageParams();
    Boolean isPaginated = true;
    PageResponseForHolidayCutoff feignResponse = new PageResponseForHolidayCutoff();
    feignResponse.setData(new HolidayCutoffDetailsResponse());
    ResponseEntity<BaseResponse<PageResponseForHolidayCutoff>> responseEntity =
        ResponseEntity.ok(BaseResponse.builder().payload(feignResponse).build());
    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    when(holidayCutoffUIFeign.getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            anyString(),
            anyString(),
            any(HolidayCutoffUIRequest.class)))
        .thenReturn(responseEntity);
    PageResponseForHolidayCutoff response =
        holidayCutoffDetailsService.getHolidayCutoffDetails(
            TestUtil.ORG_ID, pageParams, isPaginated, holidayCutoffUIRequest);
    assertNotNull(response);
    assertEquals(new HolidayCutoffDetailsResponse(), response.getData());
    verify(holidayCutoffUIFeign, times(1))
        .getHolidayCutoffDetails(
            anyString(),
            anyBoolean(),
            anyInt(),
            anyInt(),
            anyString(),
            anyString(),
            any(HolidayCutoffUIRequest.class));
  }
}
