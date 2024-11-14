/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.util;

import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.domain.dto.NodeDto;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonDashboardUtil {
  public static String[] fetchEligibleNodeServiceOption(NodeDto node) {
    return !ObjectUtils.isEmpty(node.getServiceOptionEligibilities())
        ? node.getServiceOptionEligibilities().entrySet().stream()
            .filter(entry -> Boolean.TRUE.equals(entry.getValue()))
            .map(entry -> entry.getKey().replaceAll("(?i)eligible", "").toUpperCase())
            .toArray(String[]::new)
        : new String[0];
  }

  public static Map<String, Double> fetchNodeProcessingTimeForEligibleServiceOptions(
      List<NodeCarrierResponse> nodeCarrierResponse, String[] validServiceOptions) {
    Map<String, Double> processingTime = new HashMap<>();
    if (validServiceOptions.length != 0) {
      Arrays.asList(validServiceOptions)
          .forEach(serviceOption -> processingTime.put(serviceOption, 0D));
    }
    nodeCarrierResponse.forEach(
        nodeCarrier -> {
          if (nodeCarrier.getProcessingTime() != null
              && (processingTime.containsKey(nodeCarrier.getServiceOption().toUpperCase()))) {
            processingTime.put(
                nodeCarrier.getServiceOption().toUpperCase(), nodeCarrier.getProcessingTime());
          }
        });
    return processingTime;
  }
}
