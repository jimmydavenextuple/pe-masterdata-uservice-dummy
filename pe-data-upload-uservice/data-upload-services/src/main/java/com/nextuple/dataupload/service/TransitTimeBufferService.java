/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.service;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.common.outbound.TransitBufferDetailsResponse;
import com.nextuple.transit.domain.feign.TransitBufferConfigRequestFeign;
import com.nextuple.transit.domain.outbound.TransitBufferConfigResponse;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class TransitTimeBufferService {

  private final CarrierFeign carrierFeign;
  private final TransitBufferConfigRequestFeign transitBufferConfigRequestFeign;

  public PagePayload<TransitBufferDetailsResponse> getTransitTimeBufferDetailsForCarrierServices(
      String orgId, int pageNo, int pageSize, String sortBy, String sortOrder) {
    BaseResponse<PagePayload<CarrierServiceResponse>> response =
        carrierFeign.getCarrierServiceListWithPagination(
            orgId, pageNo, pageSize, sortBy, sortOrder);

    List<TransitBufferDetailsResponse> transitBufferDetailsResponses =
        formListOfTransitBufferDetailsResponseObjects(response.getPayload().getData());

    PagePayload<TransitBufferDetailsResponse> payload = new PagePayload<>();
    payload.setPagination(response.getPayload().getPagination());
    payload.setData(transitBufferDetailsResponses);
    return payload;
  }

  private List<TransitBufferDetailsResponse> formListOfTransitBufferDetailsResponseObjects(
      List<CarrierServiceResponse> carrierServiceResponseList) {
    if (!CollectionUtils.isEmpty(carrierServiceResponseList)) {
      return carrierServiceResponseList.stream()
          .map(
              carrierServiceResponse -> {
                var carrierServiceId = carrierServiceResponse.getCarrierServiceId();
                var orgId = carrierServiceResponse.getOrgId();
                BaseResponse<List<TransitBufferConfigResponse>> response =
                    transitBufferConfigRequestFeign.getTransitBufferConfigRequests(
                        orgId, carrierServiceId);
                return TransitBufferDetailsResponse.builder()
                    .carrierServiceId(carrierServiceId)
                    .orgId(orgId)
                    .hasTransitBuffer(!CollectionUtils.isEmpty(response.getPayload()))
                    .build();
              })
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  public List<TransitBufferConfigResponse> getTransitBufferConfigRequests(
      String orgId, String carrierServiceId) {
    BaseResponse<List<TransitBufferConfigResponse>> response =
        transitBufferConfigRequestFeign.getTransitBufferConfigRequests(orgId, carrierServiceId);
    return response.getPayload();
  }
}
