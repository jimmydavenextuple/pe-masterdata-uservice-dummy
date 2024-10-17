/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.controller;

import static com.nextuple.dataupload.util.TestUtil.ORG_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeFeign;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CustomRegionDashboardControllerTest {
  @InjectMocks CustomRegionDashboardController customRegionDashboardController;

  @Mock PostalCodeFeign postalCodeFeign;

  @InjectMocks private TestUtil testUtil;

  @Mock private PageProperties pageProperties;

  @Test
  @DisplayName("Fetch Custom region information")
  void getPaginatedCustomRegionInfoTest() throws CommonServiceException, PromiseEngineException {
    List<CustomRegionInfo> customRegionInfo = testUtil.getTwoCustomRegionInfoList();
    when(postalCodeFeign.getCustomRegionInfo(
            any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(testUtil.createPagePayloadCustomRegionInfo(customRegionInfo));

    ResponseEntity<BaseResponse<PagePayload<CustomRegionInfo>>> response =
        customRegionDashboardController.getCustomRegions(
            ORG_ID,
            "CA",
            null,
            null,
            testUtil.getPageParams(
                Optional.of(2),
                Optional.of(1),
                Optional.of(TestUtil.SORT_BY),
                Optional.of(TestUtil.SORT_ORDER_DESC)));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getTotalPages(),
        "Pagination Total pages");
    Assertions.assertEquals(
        1,
        (int) response.getBody().getPayload().getPagination().getCurrentPage(),
        "Current page number");
    Assertions.assertEquals(
        customRegionInfo.size(),
        response.getBody().getPayload().getData().size(),
        "Paginated data");

    verify(postalCodeFeign, times(1))
        .getCustomRegionInfo(any(), any(), any(), any(), any(), any(), any(), any());
  }
}
