/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.persistence.util;

import com.nextuple.vendor.persistence.domain.VendorDomainDto;
import com.nextuple.vendor.persistence.entity.VendorEntity;
import java.util.*;

public class TestUtil {

  public static final String VENDOR_ID = "vendor-1";
  public static final String ORG_ID = "org-1";

  public com.nextuple.vendor.persistence.domain.VendorDomainDto getVendorDomainDto() {
    return VendorDomainDto.builder()
        .vendorId("vendor-1")
        .vendorDescription("vendor-description")
        .vendorType("Type 1")
        .orgId("org-1")
        .build();
  }

  public VendorEntity getVendorEntity() {
    return VendorEntity.builder()
        .vendorId("vendor-1")
        .vendorDescription("vendor-description")
        .vendorType("Type 1")
        .orgId("org-1")
        .build();
  }
}
