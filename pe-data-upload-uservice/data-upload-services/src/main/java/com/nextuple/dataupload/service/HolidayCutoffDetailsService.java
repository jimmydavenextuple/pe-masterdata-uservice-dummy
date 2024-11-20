/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.feign.HolidayCutoffUIFeign;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.HolidayCutoffUIRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponseForHolidayCutoff;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidayCutoffDetailsService {

  private final Logger logger = LoggerFactory.getLogger(HolidayCutoffDetailsService.class);

  @Value("${page-properties.sortOrder:ASC}")
  private String sortOrder;

  @Value("${holidaycutoff-page-properties.sortBy:ruleName}")
  private String sortBy;

  @Autowired HolidayCutoffUIFeign holidayCutoffUIFeign;

  public PageResponseForHolidayCutoff getHolidayCutoffDetails(
      String orgId,
      PageParams pageParams,
      Boolean isPaginated,
      HolidayCutoffUIRequest holidayCutoffUIRequest) {
    PageResponseForHolidayCutoff holidayCutoffDetailsResponse = null;

    try {
      ResponseEntity<BaseResponse<PageResponseForHolidayCutoff>>
          holidayCutoffDetailsResponseEntity =
              holidayCutoffUIFeign.getHolidayCutoffDetails(
                  orgId,
                  isPaginated,
                  pageParams.getPageNo().orElse(1),
                  pageParams.getPageSize().orElse(10),
                  pageParams.getSortBy().orElse(sortBy),
                  pageParams.getSortOrder().orElse(sortOrder),
                  holidayCutoffUIRequest);

      if (holidayCutoffDetailsResponseEntity != null) {
        var responseBody = holidayCutoffDetailsResponseEntity.getBody();
        if (responseBody != null) {
          holidayCutoffDetailsResponse = responseBody.getPayload();
        }
      }
    } catch (Exception e) {
      logger.error("Error fetching holiday Cutoff Details Response", e);
    }
    return holidayCutoffDetailsResponse;
  }
}
