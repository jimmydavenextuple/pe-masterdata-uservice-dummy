/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.persistence.service.impl;

import com.nextuple.item.persistence.domain.ItemSubstitutionDomainDto;
import com.nextuple.item.persistence.domain.key.ItemSubstitutionDomainKey;
import com.nextuple.item.persistence.entity.ItemSubstitutionEntity;
import com.nextuple.item.persistence.entity.key.ItemSubstitutionKey;
import com.nextuple.item.persistence.mapper.ItemSubstitutionEntityMapper;
import com.nextuple.item.persistence.repository.ItemSubstitutionRepository;
import com.nextuple.item.persistence.service.ItemSubstitutionPersistenceService;
import com.nextuple.postgres.service.CommonPersistenceService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemSubstitutionPersistenceServiceImpl
    extends CommonPersistenceService<
        ItemSubstitutionDomainDto,
        ItemSubstitutionDomainKey,
        ItemSubstitutionEntity,
        ItemSubstitutionKey,
        ItemSubstitutionRepository,
        ItemSubstitutionEntityMapper>
    implements ItemSubstitutionPersistenceService {

  @Override
  public List<ItemSubstitutionDomainDto> findByOrgIdAndPrimaryItemIdAndPrimaryUom(
      String orgId, String primaryItemId, String primaryUom) {
    return getMapper()
        .toDomain(
            getRepository()
                .findByOrgIdAndPrimaryItemIdAndPrimaryUom(orgId, primaryItemId, primaryUom));
  }

  @Override
  public List<ItemSubstitutionDomainDto> findByOrgIdAndPrimaryItemIdAndPrimaryUomList(
      String orgId, List<Pair<String, String>> primaryItemIdAndUomList) {
    return getMapper()
        .toDomain(
            getRepository()
                .findByOrgIdAndPrimaryItemIdAndPrimaryUomList(orgId, primaryItemIdAndUomList));
  }

  @Override
  public Optional<ItemSubstitutionDomainDto>
      findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
          String orgId,
          String primaryItemId,
          String primaryUom,
          String alternateItemId,
          String alternateUom) {
    ItemSubstitutionEntity entity =
        getRepository()
            .findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
                orgId, primaryItemId, primaryUom, alternateItemId, alternateUom);
    return Optional.ofNullable(entity).map(getMapper()::toDomain);
  }
}
