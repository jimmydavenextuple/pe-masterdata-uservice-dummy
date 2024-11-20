/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.domain.mapper;

import com.nextuple.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.node.carrier.domain.inbound.NodeCarrierRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCarrierRequestMapperTest {

  @InjectMocks private NodeCarrierRequestMapperImpl nodeCarrierRequestMapper;
  @InjectMocks private TestUtil testUtil;

  @Test
  void convertToNodeCarrierRequest() {
    ProcessingLeadTimesRaw processingLeadTime = testUtil.getProcessingLeadTime("U");
    NodeCarrierRequest nodeCarrierRequest =
        nodeCarrierRequestMapper.convertToNodeCarrierRequest(processingLeadTime);
    Assertions.assertNotNull(nodeCarrierRequest);
    NodeCarrierRequest nullNodeCarrierRequest =
        nodeCarrierRequestMapper.convertToNodeCarrierRequest((ProcessingLeadTimesRaw) null);
    Assertions.assertNull(nullNodeCarrierRequest);
  }
}
