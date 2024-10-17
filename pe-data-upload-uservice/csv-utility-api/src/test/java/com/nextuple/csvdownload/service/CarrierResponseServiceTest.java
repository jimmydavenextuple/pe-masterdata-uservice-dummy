/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.csvdownload.exception.CarrierServiceException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarrierResponseServiceTest {
  @Mock private CarrierFeign carrierFeign;

  @InjectMocks private CarrierResponseService carrierResponseService;

  @InjectMocks private TestUtil testUtil;

  @Test
  void getCarrierServiceTest() {
    when(carrierFeign.getCarrierServiceListByOrgId(anyString()))
        .thenReturn(
            BaseResponse.builder()
                .payload(List.of(testUtil.getCarrierServiceCalendarResponse()))
                .build());

    Assertions.assertDoesNotThrow(() -> carrierResponseService.getCarrierService(TestUtil.ORG_ID));

    verify(carrierFeign, times(1)).getCarrierServiceListByOrgId(anyString());
  }

  @Test
  void getCarrierServiceTestEmptyResponse() {
    when(carrierFeign.getCarrierServiceListByOrgId(anyString()))
        .thenReturn(BaseResponse.builder().payload(new ArrayList<>()).build());

    Assertions.assertThrows(
        CarrierServiceException.class,
        () -> carrierResponseService.getCarrierService(TestUtil.ORG_ID));

    verify(carrierFeign, times(1)).getCarrierServiceListByOrgId(anyString());
  }

  @Test
  void getCarrierServiceTestNullResponse() {
    when(carrierFeign.getCarrierServiceListByOrgId(anyString())).thenReturn(null);

    Assertions.assertThrows(
        CarrierServiceException.class,
        () -> carrierResponseService.getCarrierService(TestUtil.ORG_ID));

    verify(carrierFeign, times(1)).getCarrierServiceListByOrgId(anyString());
  }
}
