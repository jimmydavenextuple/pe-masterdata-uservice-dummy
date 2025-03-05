/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.spring.cache.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nextuple.carrier.cache.domain.CarrierCacheKey;
import com.nextuple.carrier.cache.domain.CarrierCacheValue;
import com.nextuple.carrier.cache.domain.CarrierDetails;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.response.BaseResponse;

public class TestUtil {

  public static final String ORG_ID = "Org_Id_01";
  public static final String CARRIER_ID = "Carrier_Id_01";
  public static final String CARRIER_SERVICE_ID = "Service_Id_01";
  public static final String CARRIER_NAME = "Carrier-01";
  public static final String SERVICE_NAME = "Service-01";
  public static final String SERVICE_OPTIONS = "Standard";
  private static final JsonNode CUSTOM_ATTRIBUTES =
      JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2");

  private CarrierDetails getCarrierDetails() {
    return CarrierDetails.builder()
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .orgId(ORG_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public CarrierCacheValue getCarrierCacheValue() {
    CarrierDetails carrierDetails = getCarrierDetails();
    return CarrierCacheValue.builder().carrierDetails(carrierDetails).build();
  }

  private CarrierServiceResponse getCarrierResponse() {
    return CarrierServiceResponse.builder()
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .orgId(ORG_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public BaseResponse<CarrierServiceResponse> getBaseResponseOfCarrierResponse() {
    return BaseResponse.builder()
        .message("Carrier details fetched successfully")
        .payload(getCarrierResponse())
        .build();
  }

  public CarrierCacheKey getCarrierCacheKey() {
    return CarrierCacheKey.builder()
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .orgId(ORG_ID)
        .build();
  }
}
