/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeFeign;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MarketRegionServiceTest {

  @InjectMocks private MarketRegionService marketRegionService;

  @Mock private PostalCodeFeign postalCodeFeign;
  @InjectMocks private TestUtil testUtil;

  @Test
  void getMarketRegions_Test() {
    when(postalCodeFeign.getMarketRegionsForOrgId(anyString()))
        .thenReturn(testUtil.getMarketRegionInfo());

    Assertions.assertDoesNotThrow(() -> marketRegionService.getMarketRegions(TestUtil.ORG_ID));
  }
}
