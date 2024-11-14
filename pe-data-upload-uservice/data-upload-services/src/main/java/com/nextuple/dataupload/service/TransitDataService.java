/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.jobs.framework.common.utils.ExceptionUtils;
import com.nextuple.transit.domain.feign.TransitFeign;
import com.nextuple.transit.domain.inbound.DistinctGeozonesResponse;
import feign.FeignException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class TransitDataService {

  private final Logger logger = LoggerFactory.getLogger(TransitDataService.class);
  private static final String ORG_ID = "orgId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";
  private final TransitFeign transitFeign;

  public DistinctGeozonesResponse getDistinctGeozonesList(String orgId, String carrierServiceId)
      throws CommonServiceException {
    try {
      BaseResponse<DistinctGeozonesResponse> destinationGeozones =
          transitFeign.getDistinctSourceAndDestinationGeozones(orgId, carrierServiceId);
      if (CollectionUtils.isEmpty(destinationGeozones.getPayload().getSourceGeozones())) {
        logger.error(
            "List of source geozones not available for given orgId: {} and carrierServiceId: {}",
            orgId,
            carrierServiceId);
        throw new CommonServiceException(
            "List of source geozones not available for given orgId: %s and carrierServiceId: %s"
                .formatted(orgId, carrierServiceId),
            HttpStatus.BAD_REQUEST,
            0x1778,
            Map.of(
                ORG_ID,
                FieldError.builder().rejectedValue(orgId).build(),
                CARRIER_SERVICE_ID,
                FieldError.builder().rejectedValue(carrierServiceId).build()));
      }
      if (CollectionUtils.isEmpty(destinationGeozones.getPayload().getDestinationGeozones())) {
        logger.error(
            "List of destination geozones not available for given orgId: {} and carrierServiceId: {}",
            orgId,
            carrierServiceId);
        throw new CommonServiceException(
            "List of destination geozones not available for given orgId: %s and carrierServiceId: %s"
                .formatted(orgId, carrierServiceId),
            HttpStatus.BAD_REQUEST,
            0x1778,
            Map.of(
                ORG_ID,
                FieldError.builder().rejectedValue(orgId).build(),
                CARRIER_SERVICE_ID,
                FieldError.builder().rejectedValue(carrierServiceId).build()));
      }
      return destinationGeozones.getPayload();
    } catch (FeignException e) {
      logger.error("Feign exception while fetching distinct source and destination geozones", e);
      var errorResponse = ExceptionUtils.parseFeignException(e);
      throw new CommonServiceException(
          errorResponse.getMessage(),
          e,
          HttpStatus.BAD_REQUEST,
          0x1778,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              CARRIER_SERVICE_ID,
              FieldError.builder().rejectedValue(carrierServiceId).build()));
    } catch (Exception e) {
      logger.error("Error while fetching distinct source and destination geozones", e);
      throw new CommonServiceException(
          e.getMessage(),
          e,
          HttpStatus.BAD_REQUEST,
          0x1778,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              CARRIER_SERVICE_ID,
              FieldError.builder().rejectedValue(carrierServiceId).build()));
    }
  }
}
