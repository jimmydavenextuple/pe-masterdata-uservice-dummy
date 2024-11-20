/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.domain.mapper;

import com.nextuple.postal.code.timezone.api.domain.dto.MarketRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.inbound.PostalCodeRequest;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.postal.code.timezone.api.domain.projection.MarketRegionProjection;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeDomainDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PostalCodeMapper {
  PostalCodeDomainDto convertToPostalCodeEntity(PostalCodeRequest postalCodeRequest);

  PostalCodeResponse convertToPostalCodeResponse(PostalCodeDomainDto postalCodeDomainDto);

  List<PostalCodeResponse> convertToPostalCodeResponseList(
      List<PostalCodeDomainDto> postalCodeDomainDtos);

  void updatePostalCodeEntity(
      PostalCodeRequest postalCodeRequest, @MappingTarget PostalCodeDomainDto postalCodeDomainDto);

  List<MarketRegionInfo> convertToMarketRegionInfo(
      List<MarketRegionProjection> marketRegionProjectionList);
}
