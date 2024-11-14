/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.domain.mapper;

import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.csvdownload.domain.pojo.TransitDataErrorLogsPojo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransitDataRequestMapperTest {
  @InjectMocks private TestUtil testUtil;
  TransitDataRequestMapper mapper = Mappers.getMapper(TransitDataRequestMapper.class);

  @Test
  void convertToTransitDataErrorLogsPojo() {

    TransitDataErrorLogsPojo transitDataErrorLogsPojo =
        mapper.convertToTransitDataErrorLogsPojo(testUtil.getAddTransitDataRequest());
    TransitDataErrorLogsPojo nullTransitDataErrorLogsPojo =
        mapper.convertToTransitDataErrorLogsPojo(null);
    Assertions.assertNull(nullTransitDataErrorLogsPojo);
    Assertions.assertNotNull(transitDataErrorLogsPojo);
    Assertions.assertEquals(TestUtil.ORG_ID, transitDataErrorLogsPojo.getOrgId());
  }
}
