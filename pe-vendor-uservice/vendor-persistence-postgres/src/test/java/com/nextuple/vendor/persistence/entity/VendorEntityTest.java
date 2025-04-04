/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.vendor.persistence.entity.key.VendorKey;
import org.junit.jupiter.api.Test;

public class VendorEntityTest {

  public static final String NODE_ID = "node-1";
  public static final String ORG_ID = "org-1";

  @Test
  void vendorEntityTest() {
    VendorEntity vendorEntity = new VendorEntity();
    assertNull(vendorEntity.getVendorId());
    assertNull(vendorEntity.getVendorDescription());
    assertNull(vendorEntity.getVendorType());
    assertNull(vendorEntity.getOrgId());
  }

  @Test
  void vendorIdNullTest() {
    VendorKey vendorKey = new VendorKey();
    assertNull(vendorKey.getVendorId());
    assertNull(vendorKey.getOrgId());
  }

  @Test
  void vendorIdTest() {
    VendorKey vendorKey = new VendorKey(NODE_ID, ORG_ID);
    assertNotNull(vendorKey.getVendorId());
    assertNotNull(vendorKey.getOrgId());
  }
}
