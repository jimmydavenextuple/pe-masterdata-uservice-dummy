/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.persistence.service.impl;

import com.nextuple.common.exception.CommonServiceException;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

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
  public List<ItemSubstitutionDomainDto> findByPrimaryItemIdAndPrimaryUom(
      String primaryItemId, String primaryUom) throws CommonServiceException {
    try {
      return getMapper()
          .toDomain(getRepository().findByPrimaryItemIdAndPrimaryUom(primaryItemId, primaryUom));
    } catch (Exception e) {
      log.debug("Unable to find item substitution", e);
      throw new CommonServiceException(
          "Error while finding item substitution", HttpStatus.INTERNAL_SERVER_ERROR, null, null);
    }
  }

  @Override
  public Optional<ItemSubstitutionDomainDto>
      findByPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
          String primaryItemId, String primaryUom, String alternateItemId, String alternateUom)
          throws CommonServiceException {
    try {
      ItemSubstitutionDomainDto dto =
          this.getRepository()
              .findByPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
                  primaryItemId, primaryUom, alternateItemId, alternateUom);
      return Optional.ofNullable(dto);
    } catch (Exception e) {
      log.error("Unable to find item substitution", e);
      throw new CommonServiceException(
          "Error while finding item substitution", HttpStatus.INTERNAL_SERVER_ERROR, null, null);
    }
  }
}
