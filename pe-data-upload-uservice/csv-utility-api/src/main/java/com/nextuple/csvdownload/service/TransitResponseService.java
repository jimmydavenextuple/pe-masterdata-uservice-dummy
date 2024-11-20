/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.exception.TransitServiceException;
import com.nextuple.transit.domain.dto.TransitTimeEntriesDto;
import com.nextuple.transit.domain.feign.TransitFeign;
import com.nextuple.transit.domain.inbound.TransitDetailsRequest;
import com.nextuple.transit.domain.outbound.TransitResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class TransitResponseService {

  private final TransitFeign transitFeign;

  private final Logger logger = LoggerFactory.getLogger(TransitResponseService.class);

  public List<TransitResponse> getTransitDetails(
      String orgId, String carrierServiceId, List<String> destinationGeoZones)
      throws TransitServiceException {
    logger.debug("Processing get geoZone list for orgId and city");
    var transitDetailsRequest = new TransitDetailsRequest();
    transitDetailsRequest.setDestinationGeozones(destinationGeoZones);
    BaseResponse<List<TransitResponse>> response =
        transitFeign.getTransitTimeDetailsForDestinationGeoZonesList(
            orgId, carrierServiceId, transitDetailsRequest);
    if (response != null && !CollectionUtils.isEmpty(response.getPayload())) {
      return response.getPayload();
    } else {
      logger.error(
          "Transit details does not exist for given orgId, carrierServiceId and destinationGeoZones");
      throw new TransitServiceException(
          "Transit details does not exist for given orgId, carrierServiceId and destinationGeoZones",
          orgId,
          carrierServiceId);
    }
  }

  public TransitTimeEntriesDto getTransitTimeEntries(String orgId, String carrierServiceId)
      throws TransitServiceException {
    try {
      BaseResponse<TransitTimeEntriesDto> response =
          transitFeign.getTransitTimeEntries(orgId, carrierServiceId);
      return response.getPayload();
    } catch (Exception e) {
      throw new TransitServiceException(
          "Transit details does not exist for given orgId and carrierServiceId  ",
          orgId,
          carrierServiceId);
    }
  }
}
