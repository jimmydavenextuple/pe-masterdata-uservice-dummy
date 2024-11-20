/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.service.HolidayCutoffDetailsService;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponseForHolidayCutoff;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class HolidayCutoffDashboardControllerTest {

  @InjectMocks HolidayCutoffDashboardController holidayCutoffDashboardController;

  @Mock HolidayCutoffDetailsService holidayCutoffDetailsService;

  @Test
  @DisplayName("Get Holiday cutoff details Controller test")
  void getHolidayCutoffDetailsTest() {
    PageParams pageParams = new PageParams();
    Boolean isPaginated = true;
    HolidayCutoffUIRequest holidayCutoffUIRequest =
        HolidayCutoffUIRequest.builder().sourcingAttributesDefinitionId(1L).build();
    PageResponseForHolidayCutoff pageResponse = new PageResponseForHolidayCutoff();

    when(holidayCutoffDetailsService.getHolidayCutoffDetails(
            anyString(), any(PageParams.class), anyBoolean(), any(HolidayCutoffUIRequest.class)))
        .thenReturn(pageResponse);

    ResponseEntity<BaseResponse<PageResponseForHolidayCutoff>> responseEntity =
        holidayCutoffDashboardController.getHolidayCutoffDetails(
            TestUtil.ORG_ID, pageParams, isPaginated, holidayCutoffUIRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        "Holiday cutoff details List fetched successfully", responseEntity.getBody().getMessage());
    assertEquals(pageResponse, responseEntity.getBody().getPayload());

    verify(holidayCutoffDetailsService, times(1))
        .getHolidayCutoffDetails(TestUtil.ORG_ID, pageParams, isPaginated, holidayCutoffUIRequest);
  }
}
