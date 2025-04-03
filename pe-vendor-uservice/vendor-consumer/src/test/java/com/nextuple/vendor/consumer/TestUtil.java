/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.consumer;

import com.nextuple.common.enums.ActionEnum;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.vendor.consumer.dto.VendorFeedDto;
import com.nextuple.vendor.domain.outbound.VendorResponse;
import com.nextuple.vendor.persistence.domain.VendorDomainDto;

public class TestUtil {

  public VendorFeedDto createVendorFeedDto() {
    return VendorFeedDto.builder()
        .vendorId("Vendor-1")
        .vendorDescription("Vendor 1")
        .vendorType("Store")
        .orgId("NEXTUPLE")
        .build();
  }

  public ResponseDto createResponseDto(int recordNo, int statusCode, String message) {
    return ResponseDto.builder().recordNo(recordNo).statusCode(statusCode).message(message).build();
  }

  public BaseResponse<VendorResponse> getBaseResponseOfVendorFeed(String message) {
    return BaseResponse.builder()
        .message(message)
        .success(true)
        .payload(getVendorResponse())
        .build();
  }

  public VendorResponse getVendorResponse() {
    return VendorResponse.builder()
        .vendorId("Vendor-1")
        .vendorDescription("Vendor 1")
        .vendorType("Store")
        .orgId("NEXTUPLE")
        .build();
  }

  public VendorDomainDto getVendorDomainDto() {
    return VendorDomainDto.builder()
        .vendorId("Vendor-1")
        .vendorDescription("Vendor 1")
        .vendorType("Store")
        .orgId("NEXTUPLE")
        .build();
  }

  public BatchResponse getVendorBatchResponse(
      int totalRecords, int successfulRecords, int failedRecords) {
    return BatchResponse.builder()
        .totalRecords(totalRecords)
        .successfulRecords(successfulRecords)
        .failedRecords(failedRecords)
        .build();
  }

  public BatchRequest<VendorFeedDto> getVendorFeedRequest(ActionEnum action) {
    BatchRequest<VendorFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createVendorFeedDto());
    return batchRequest;
  }
}
