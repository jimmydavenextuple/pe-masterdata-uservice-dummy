/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.persistence;

import com.nextuple.carrier.domain.inbound.CarrierServiceRequest;
import com.nextuple.carrier.persistence.domain.CarrierServiceDomainDto;

public class TestUtil {

  public static final String NODE_ID = "node-1";
  public static final String ORG_ID = "org-1";
  public static final String CARRIER_ID = "carrier-1";
  public static final String CARRIER_SERVICE_ID = "carrier-service-1";
  public static final String CARRIER_SERVICE_ID_2 = "carrier-service-2";
  public static final String CARRIER_NAME = "carrier-name-1";
  public static final String SERVICE_NAME = "service-name-1";
  public static final String SERVICE_OPTIONS = "service-options-1";
  public static final String SERVICE_OPTIONS_2 = "service-options-2";
  public static final String SORT_BY = "carrierId";
  public static final String SORT_ORDER_DESC = "desc";
  public static final String SORT_ORDER_ASC = "ASC";
  private static final String CARRIER_ID_2 = "carrier-2";
  public static final String CONFIG_KEY = "service-options";
  public static final String CONFIG_VALUE = "SDND, EXPRESS, STANDARD";

  public CarrierServiceDomainDto getCarrierServiceDomainDto() {
    return CarrierServiceDomainDto.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public CarrierServiceRequest getCarrierServiceRequest() {
    return CarrierServiceRequest.builder()
        .orgId(ORG_ID)
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }
}
