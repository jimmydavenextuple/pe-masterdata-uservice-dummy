/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class TransitBufferConfigRequestEntityTest {

  @Test
  void transitBufferConfigRequestEntityTest() {
    TransitBufferConfigRequestEntity transitBufferConfigRequestEntity =
        new TransitBufferConfigRequestEntity();
    assertNull(transitBufferConfigRequestEntity.getId());
    assertNull(transitBufferConfigRequestEntity.getOrgId());
    assertNull(transitBufferConfigRequestEntity.getCarrierServiceId());
    assertNull(transitBufferConfigRequestEntity.getBufferDays());
    assertNull(transitBufferConfigRequestEntity.getStartDate());
    assertNull(transitBufferConfigRequestEntity.getEndDate());
    assertNull(transitBufferConfigRequestEntity.getStatus());
    assertNull(transitBufferConfigRequestEntity.getParentRequestId());
    assertNull(transitBufferConfigRequestEntity.getFileMetaDataId());
    assertNull(transitBufferConfigRequestEntity.getDownloadFileMetaDataId());
  }

  @Test
  void transitBufferConfigRequestEntitySetValuesTest() {
    TransitBufferConfigRequestEntity transitBufferConfigRequestEntity =
        getTransitBufferConfigRequestEntity();
    assertNotNull(transitBufferConfigRequestEntity.getId());
    assertNotNull(transitBufferConfigRequestEntity.getOrgId());
    assertNotNull(transitBufferConfigRequestEntity.getCarrierServiceId());
    assertNotNull(transitBufferConfigRequestEntity.getBufferDays());
    assertNotNull(transitBufferConfigRequestEntity.getParentRequestId());
    assertNotNull(transitBufferConfigRequestEntity.getFileMetaDataId());
    assertNotNull(transitBufferConfigRequestEntity.getDownloadFileMetaDataId());
  }

  public TransitBufferConfigRequestEntity getTransitBufferConfigRequestEntity() {
    return TransitBufferConfigRequestEntity.builder()
        .id(1L)
        .orgId("orgId")
        .carrierServiceId("carrierServiceId")
        .bufferDays(5.0)
        .parentRequestId(2L)
        .fileMetaDataId(3L)
        .downloadFileMetaDataId(4L)
        .build();
  }
}
