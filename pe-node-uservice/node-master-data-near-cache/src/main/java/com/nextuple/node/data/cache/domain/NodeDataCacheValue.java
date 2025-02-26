/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.data.cache.domain;

import com.nextuple.common.annotation.AttributePath;
import com.nextuple.common.pojo.AdditionalAttributes;
import com.nextuple.core.cache.domain.CacheValue;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
public class NodeDataCacheValue extends AdditionalAttributes implements CacheValue {
  @AttributePath(path = "/nodeId")
  private String nodeId;

  private String orgId;
  private String street;
  private String city;
  private String latitude;
  private String longitude;
  private String timezone;
  private String state;

  @AttributePath(path = "/nodeZipCode")
  private String zipCode;

  private String country;

  @AttributePath(path = "/nodeType")
  private String nodeType;

  @AttributePath(path = "/nodeLabourTier")
  private String nodeLabourTier;

  private Boolean isActive;

  private String startWorkingTime;

  private String lastWorkingTime;

  private Map<String, Boolean> serviceOptionEligibilities;
  private Boolean shipToHome;
  private Boolean bopisEligible;

  @Override
  public boolean isUndefined() {
    return false;
  }

  public Map<String, Object> updateContextMap(String rootPath, Map<String, Object> contextMap) {
    contextMap.put(rootPath + "/nodeId", this.getNodeId());
    contextMap.put(rootPath + "/nodeZipCode", this.getZipCode());
    contextMap.put(rootPath + "/nodeType", this.getNodeType());
    contextMap.put(rootPath + "/nodeLabourTier", this.getNodeLabourTier());

    return contextMap;
  }
}
