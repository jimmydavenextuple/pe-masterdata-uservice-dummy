/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.exception;

import com.nextuple.node.carrier.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NodeCarrierDomainExceptionTest {

  @Test
  @DisplayName("Testing node carrier domain exception")
  void constructTest() {
    NodeCarrierDomainException nodeCarrierDomainException =
        new NodeCarrierDomainException(
            "Error while saving the node carrier",
            TestUtil.NODE_ID,
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);

    Assertions.assertEquals(
        "Error while saving the node carrier", nodeCarrierDomainException.getMessage());
    Assertions.assertEquals(TestUtil.NODE_ID, nodeCarrierDomainException.getNodeId());
    Assertions.assertEquals(TestUtil.ORG_ID, nodeCarrierDomainException.getOrgId());
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID, nodeCarrierDomainException.getCarrierServiceId());
    Assertions.assertEquals(TestUtil.SERVICE_OPTION, nodeCarrierDomainException.getServiceOption());
  }
}
