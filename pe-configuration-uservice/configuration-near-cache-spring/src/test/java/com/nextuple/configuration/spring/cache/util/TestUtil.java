/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.configuration.spring.cache.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.configuration.cache.domain.TenantConfigdataCacheKey;
import com.nextuple.configuration.cache.domain.TenantConfigdataCacheValue;
import com.nextuple.configuration.outbound.TenantConfigdataResponse;

public class TestUtil {
  public static final JsonNode CUSTOM_ATTRIBUTES =
      JsonNodeFactory.instance.objectNode().put("key1", "value1").put("key2", "value2");

  public TenantConfigdataCacheKey getTenantConfigdataCacheKey() {
    return TenantConfigdataCacheKey.builder()
        .orgId("org-1")
        .configKey("custom-key")
        .build();
  }

  public TenantConfigdataCacheValue getTenantConfigCacheValue() {
    return TenantConfigdataCacheValue.builder()
        .id(1L)
        .orgId("org-1")
        .configKey("custom-key")
        .configValue("SDND,EXPRESS")
        .customAttributes(CUSTOM_ATTRIBUTES)
        .build();
  }

  public BaseResponse<TenantConfigdataResponse> getTenantConfigdataResponse() {
    BaseResponse<TenantConfigdataResponse> baseResponse = new BaseResponse<>();
    TenantConfigdataResponse tenantConfigdataResponse =
        TenantConfigdataResponse.builder()
            .id(1L)
            .orgId("org-1")
            .configKey("custom-key")
            .configValue("SDND,EXPRESS")
            .customAttributes(CUSTOM_ATTRIBUTES)
            .build();
    baseResponse.setPayload(tenantConfigdataResponse);
    return baseResponse;
  }
}
