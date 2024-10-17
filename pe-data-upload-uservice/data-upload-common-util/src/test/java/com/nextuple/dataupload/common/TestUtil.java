/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.common;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class TestUtil {

  public static final String ORG_ID = "NEXTUPLE_GR";

  public static final String tenantServiceOption = "SDND,EXPRESS";
  public static final String NODE_ID = "Node_Id_01";
  public static final String CARRIER_SERVICE_ID = "Carrier_Service_Id_01";
  public static final String SERVICE_OPTION = "Standard";
  public static final Double PROCESSING_TIME = 10.0;
  public static final String LAST_PICK_UP_TIME = "5:00 PM";
  private static final String SERVICE_OPTION_2 = "SDND";

  public static final String DEFAULT = "DEFAULT";
  public static final List<String> tenantServiceOptionExpected =
      List.of("sdndEligible", "expressEligible");

  public static final Map<String, String> getEmptyCustomAttribute() {
    Map<String, String> mockCustomAttributes = new HashMap<>();
    return mockCustomAttributes;
  }

  public static final Map<String, String> getTenantCustomAttribute() {
    Map<String, String> mockCustomAttributes = new HashMap<>();
    mockCustomAttributes.put("key1", "value1");
    mockCustomAttributes.put("key2", "value2");
    return mockCustomAttributes;
  }

  public static Map<String, String> getTenantLinesCustomAttribute() {
    Map<String, String> mockCustomAttributes = new HashMap<>();
    mockCustomAttributes.put("key3", "value3");
    mockCustomAttributes.put("key4", "value4");
    return mockCustomAttributes;
  }

  public static List<String> getExpectedEDDHeaders() {
    List<String> expectedHeaders =
        Arrays.asList(
            "orgId",
            "serviceOptions",
            "cartId",
            "sessionId",
            "pageName",
            "shipToAddress_zipCode",
            "shipToAddress_region",
            "shipToAddress_state",
            "shipToAddress_country",
            "shipToAddress_timezone",
            "lines_requiredQty",
            "lines_lineId",
            "lines_serviceOption",
            "lines_item_itemId",
            "lines_item_productClass",
            "lines_item_unitOfMeasure",
            "lines_shipToAddress_zipCode",
            "lines_shipToAddress_region",
            "lines_shipToAddress_state",
            "lines_shipToAddress_country",
            "lines_shipToAddress_timezone");
    return expectedHeaders;
  }

  public static List<String> getExpectedEDDHeadersWithCustomAttributes() {
    List<String> expectedHeaders =
        Arrays.asList(
            "orgId",
            "serviceOptions",
            "cartId",
            "sessionId",
            "pageName",
            "shipToAddress_zipCode",
            "shipToAddress_region",
            "shipToAddress_state",
            "shipToAddress_country",
            "shipToAddress_timezone",
            "lines_requiredQty",
            "lines_lineId",
            "lines_serviceOption",
            "lines_item_itemId",
            "lines_item_productClass",
            "lines_item_unitOfMeasure",
            "lines_shipToAddress_zipCode",
            "lines_shipToAddress_region",
            "lines_shipToAddress_state",
            "lines_shipToAddress_country",
            "lines_shipToAddress_timezone",
            "lines_customAttributes_key3",
            "lines_customAttributes_key4",
            "customAttributes_key1",
            "customAttributes_key2");
    return expectedHeaders;
  }

  public static final String nodeCsvData =
      "action,nodeId,orgId,street,city,state,zipCode,country,latitude,longitude,timezone,shipToHome,bopisEligible,nodeType,isActive\n"
          + "CREATE,1957,NEXTUPLE,Calgary Downtown,Calgary,AB,T2P 1B5,CA,52.1977,-113.8721,America/Edmonton,TRUE,FALSE,MFC,TRUE\n"
          + "UPDATE,1957,NEXTUPLE,Calgary Downtown,Calgary,AB,T2P 1B5,CA,52.1977,-113.8721,America/Edmonton,TRUE,FALSE,MFC,TRUE\n"
          + "DELETE,1957,NEXTUPLE,Calgary Downtown,Calgary,AB,T2P 1B5,CA,52.1977,-113.8721,America/Edmonton,TRUE,FALSE,MFC,TRUE";

  public static final String nodeCsvExpectedData =
      "action,nodeId,orgId,street,city,state,zipCode,country,latitude,longitude,timezone,shipToHome,bopisEligible,nodeType,nodeLabourTier,isActive,"
          + tenantServiceOption.toLowerCase()
          + "Eligible\n"
          + "CREATE,1957,"
          + ORG_ID
          + ",Calgary Downtown,Calgary,AB,T2P 1B5,CA,52.1977,-113.8721,America/Edmonton,TRUE,FALSE,MFC,tier1,TRUE,FALSE\n"
          + "UPDATE,1957,"
          + ORG_ID
          + ",Calgary Downtown,Calgary,AB,T2P 1B5,CA,52.1977,-113.8721,America/Edmonton,TRUE,FALSE,MFC,tier1,TRUE,FALSE\n"
          + "DELETE,1957,"
          + ORG_ID
          + ",Calgary Downtown,Calgary,AB,T2P 1B5,CA,52.1977,-113.8721,America/Edmonton,TRUE,FALSE,MFC,tier1,TRUE,FALSE";

  public static final String transitCsvData =
      "orgId,NEXTUPLE,,,,,,,,,\n"
          + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
          + "Destination geoZone / Source geoZone ->,SGZ1,SGZ2,SGZ3,SGZ4,SGZ5,SGZ6,SGZ7,SGZ8,SGZ9,SGZ10\n"
          + "DGZ1,10,9.96,9.96,DELETE,DELETE,7,7,8.09,DELETE,DELETE\n"
          + "DGZ2,DELETE,DELETE,DELETE,DELETE,9,7.81,7.81,7.89,7.89,D\n"
          + "DGZ3,10,9,9,9,9,9.5,9.5,7,7.89,7.89\n"
          + "DGZ4,10,DELETE,DELETE,DELETE,9.96,8.09,8.09,7.89,8.09,8.09\n"
          + "DGZ5,10,9.96,9.96,9.96,9.96,7.81,7.81,DELETE,DELETE,DELETE\n"
          + "DGZ6,DELETE,9.96,DELETE,DELETE,DELETE,7,7,8.09,8.09,8.09\n"
          + "DGZ7,DELETE,DELETE,DELETE,10,10,7.81,7.81,7.89,7.89,7.89\n"
          + "DGZ8,10,9.96,9.96,9.96,9.96,7.81,7.81,8.09,6,6\n"
          + "DGZ9,10,9.5,9.5,9.5,9.5,DELETE,DELETE,DELETE,DELETE,DELETE\n"
          + "DGZ10,DELETE,DELETE,DELETE,8,8,8.09,8.09,DELETE,DELETE,DELETE";

  public static final String CarrierServiceCsvData =
      "action,orgId,carrierId,carrierName,carrierServiceId,serviceName,serviceOptions\n"
          + "DELETE,NEXTUPLE,A_SDND,ALL,ALL-SDND,All SDND Carrier Services,SDND\n"
          + "CREATE,NEXTUPLE,GoFor,GoFor,GoFor-SDND,GoFor,SDND\n"
          + "UPDATE,NEXTUPLE,GoFor,GoFor,GoFor-SDND,GoFor,SDND\n";

  public static final String CONFIG_KEY = "service-options";
  public static final String CONFIG_VALUE = "SDND,EXPRESS";

  public BaseResponse<TenantConfigdataResponse> getTenantConfigdataBaseResponse() {
    TenantConfigdataResponse tenantConfigdataResponse =
        TenantConfigdataResponse.builder().configKey(CONFIG_KEY).configValue(CONFIG_VALUE).build();
    return BaseResponse.builder().payload(tenantConfigdataResponse).build();
  }

  public static final String CUSTOM_ATTRIBUTES_CONFIG_VALUE = "{key1: value1, key2: value2}";
  public static final String CUSTOM_ATTRIBUTES_CONFIG_KEY = "custom-attributes";

  public BaseResponse<TenantConfigdataResponse> getTenantCustomAttributeConfigdataBaseResponse() {
    TenantConfigdataResponse tenantConfigdataResponse =
        TenantConfigdataResponse.builder()
            .configKey(CUSTOM_ATTRIBUTES_CONFIG_KEY)
            .configValue(CUSTOM_ATTRIBUTES_CONFIG_VALUE)
            .build();
    return BaseResponse.builder().payload(tenantConfigdataResponse).build();
  }

  public static final String LINES_CUSTOM_ATTRIBUTES_CONFIG_VALUE = "{key3: value3, key4: value4}";
  public static final String LINES_CUSTOM_ATTRIBUTES_CONFIG_KEY = "lines-custom-attributes";

  public BaseResponse<TenantConfigdataResponse>
      getTenantLinesCustomAttributeConfigdataBaseResponse() {
    TenantConfigdataResponse tenantConfigdataResponse =
        TenantConfigdataResponse.builder()
            .configKey(LINES_CUSTOM_ATTRIBUTES_CONFIG_KEY)
            .configValue(LINES_CUSTOM_ATTRIBUTES_CONFIG_VALUE)
            .build();
    return BaseResponse.builder().payload(tenantConfigdataResponse).build();
  }

  public static final Map<String, String> CUSTOM_ATTRIBUTES_EXPECTED_CONFIG_MAP =
      new HashMap<>(Map.of("key1", "value1", "key2", "value2"));

  public static final Map<String, String> LINES_CUSTOM_ATTRIBUTES_EXPECTED_CONFIG_MAP =
      new HashMap<>(Map.of("key3", "value3", "key4", "value4"));
}
