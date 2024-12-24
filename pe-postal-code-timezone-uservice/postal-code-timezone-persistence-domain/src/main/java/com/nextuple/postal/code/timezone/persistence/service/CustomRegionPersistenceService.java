/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.postal.code.timezone.persistence.service;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.postal.code.timezone.persistence.domain.CustomRegionDomainDto;
import com.nextuple.postal.code.timezone.persistence.domain.key.CustomRegionDomainKey;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface CustomRegionPersistenceService
    extends DomainPersistenceService<CustomRegionDomainDto, CustomRegionDomainKey> {
  CustomRegionDomainDto saveCustomRegion(CustomRegionDomainDto customRegion);

  Optional<CustomRegionDomainDto> fetchRegionByOrgIdAndId(String orgId, String customRegionId);

  Optional<CustomRegionDomainDto> fetchRegionByOrgIdAndCustomRegionName(
      String orgId, String customRegionName);

  void deleteCustomRegion(CustomRegionDomainDto customRegion);

  Page<CustomRegionDomainDto> getCustomRegionListByOrgId(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws PromiseEngineException;

  List<CustomRegionDomainDto> fetchCustomRegionByNamesAndOrgId(
      List<String> customRegionNames, String orgId) throws PromiseEngineException;

  Optional<List<CustomRegionDomainDto>> fetchCustomRegionsByCustomRegionIdsAndNamesAndOrgId(
      List<String> customRegionIds, List<String> customRegionNames, String orgId)
      throws PromiseEngineException;

  List<CustomRegionDomainDto> fetchCustomRegionsByIdsAndOrgId(
      List<String> customRegionIds, String orgId) throws PromiseEngineException;
}
