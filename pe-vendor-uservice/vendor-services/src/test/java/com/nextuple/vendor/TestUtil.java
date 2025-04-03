/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor;

import com.nextuple.vendor.domain.inbound.VendorRequest;
import com.nextuple.vendor.domain.inbound.VendorUpdationRequest;
import com.nextuple.vendor.domain.outbound.VendorResponse;
import com.nextuple.vendor.persistence.domain.VendorDomainDto;

public class TestUtil {

  public static final String VENDOR_ID = "vendor-1";
  public static final String ORG_ID = "org-1";

  public VendorRequest getVendorRequest() {
    return VendorRequest.builder()
        .vendorId("vendor-1")
        .vendorDescription("vendor-description")
        .vendorType("Type 1")
        .orgId("org-1")
        .build();
  }

  public VendorResponse getVendorResponse() {
    return VendorResponse.builder()
        .vendorId("vendor-1")
        .vendorDescription("vendor-description")
        .vendorType("Type 1")
        .orgId("org-1")
        .build();
  }

  public VendorUpdationRequest getVendorUpdationRequest() {
    return VendorUpdationRequest.builder()
        .vendorDescription("vendor-description")
        .vendorType("Type 1")
        .build();
  }

  public VendorResponse getUpdatedVendorResponse() {
    return VendorResponse.builder()
        .vendorId("vendor-1")
        .vendorDescription("vendor-description")
        .vendorType("Type 1")
        .orgId("org-1")
        .build();
  }

  public VendorDomainDto getVendorDomainDto(String vendorId) {
    return VendorDomainDto.builder()
        .vendorId(vendorId)
        .vendorDescription("vendor-description")
        .vendorType("Type 1")
        .orgId("org-1")
        .build();
  }
}
