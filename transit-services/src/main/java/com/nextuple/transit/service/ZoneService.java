/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.transit.domain.inbound.ZoneRequest;
import com.nextuple.transit.domain.inbound.ZoneUpdateRequest;
import com.nextuple.transit.domain.outbound.ZoneResponse;
import java.util.List;

public interface ZoneService {

  ZoneResponse addZoneData(ZoneRequest zoneRequest)
      throws PromiseEngineException, CommonServiceException;

  ZoneResponse updateZone(
      String orgId,
      String sourceGeozone,
      String destinationGeozone,
      String carrierServiceId,
      ZoneUpdateRequest zoneUpdateRequest)
      throws PromiseEngineException, CommonServiceException;

  ZoneResponse getZoneDetails(
      String orgId, String sourceGeozone, String destinationGeozone, String carrierServiceId)
      throws PromiseEngineException, CommonServiceException;

  ZoneResponse deleteZoneDetails(
      String orgId, String sourceGeozone, String destinationGeozone, String carrierServiceId)
      throws PromiseEngineException, CommonServiceException;

  List<ZoneResponse> getZoneDetailsList(String orgId, String destinationGeozone)
      throws PromiseEngineException, CommonServiceException;
}
