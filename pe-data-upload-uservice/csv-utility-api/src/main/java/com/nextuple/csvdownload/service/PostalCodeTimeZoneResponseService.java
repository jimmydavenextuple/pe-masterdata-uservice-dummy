/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.exception.CustomRegionServiceException;
import com.nextuple.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionDto;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeFeign;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class PostalCodeTimeZoneResponseService {

  private final PostalCodeFeign postalCodeFeign;

  private final Logger logger = LoggerFactory.getLogger(PostalCodeTimeZoneResponseService.class);

  @Value("${download-page-size.custom-region}")
  private int pageSize;

  public List<String> getFSAsByOrgIdAndState(String orgId, String state)
      throws PostalCodeTimezoneServiceException {
    logger.debug("Processing get geoZone list for orgId and state");
    BaseResponse<List<String>> response =
        postalCodeFeign.getPostalCodePrefixForOrgIdAndState(orgId, state);
    if (response != null && !CollectionUtils.isEmpty(response.getPayload())) {
      return response.getPayload();
    } else {
      logger.error("geoZone does not exist for given orgId and destination region / state");
      throw new PostalCodeTimezoneServiceException(
          "geoZone does not exist for given orgId and destination region / state",
          orgId,
          state,
          null);
    }
  }

  public List<PostalCodeResponse> getPostalCodeTimeZoneByOrgIdAndCountry(
      String orgId, String country) throws PostalCodeTimezoneServiceException {
    logger.debug("Processing get zip code timezone for orgId and country");
    BaseResponse<List<PostalCodeResponse>> response =
        postalCodeFeign.getPostalCodeTimeZoneForOrgIdAndCountry(orgId, country);
    if (response != null && !CollectionUtils.isEmpty(response.getPayload())) {
      return response.getPayload();
    } else {
      logger.error("Market Regions does not exist for given orgId and country");
      throw new PostalCodeTimezoneServiceException(
          "Market Regions does not exist for given orgId and country", orgId, null, country);
    }
  }

  public List<CustomRegionDto> getCustomRegionsByOrgId(String orgId) {
    logger.debug("Processing get custom regions for orgId");
    PagePayload<CustomRegionDto> pagePayload =
        postalCodeFeign.getCustomRegionList(orgId, 1, pageSize, null, null).getPayload();

    int totalPages = pagePayload.getPagination().getTotalPages();
    int currentPageNo = pagePayload.getPagination().getCurrentPage();

    List<CustomRegionDto> customRegionDtoList = new ArrayList<>(pagePayload.getData());

    while (currentPageNo < totalPages) {
      currentPageNo = currentPageNo + 1;
      PagePayload<CustomRegionDto> pagePayload1 =
          postalCodeFeign
              .getCustomRegionList(orgId, currentPageNo, pageSize, null, null)
              .getPayload();
      customRegionDtoList.addAll(pagePayload1.getData());
    }

    return customRegionDtoList;
  }

  public CustomRegionResponse getCustomRegionsByOrgIdAndRegionId(String orgId, String regionId)
      throws CustomRegionServiceException {
    logger.debug("Processing get custom regions for orgId and regionId");
    BaseResponse<CustomRegionResponse> customRegionBaseResponse =
        postalCodeFeign.fetchCustomRegionDetailsByOrgIdAndId(orgId, regionId);

    if (!customRegionBaseResponse.isSuccess()) {
      logger.error("Custom Region does not exist for given orgId and regionId");
      throw new CustomRegionServiceException(
          "Custom Region does not exist for given orgId and regionId", orgId, regionId);
    }
    return customRegionBaseResponse.getPayload();
  }
}
