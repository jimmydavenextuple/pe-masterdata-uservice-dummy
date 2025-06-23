/*
 *  Copyright (c) 2025, Nextuple, Inc. and/or its affiliates. All rights reserved.
 *  The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 *  The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.item.domain.inbound.DeleteItemSubstitutionRequest;
import com.nextuple.item.domain.inbound.UpsertItemSubstitutionRequest;
import com.nextuple.item.domain.mapper.ItemSubstitutionMapper;
import com.nextuple.item.domain.outbound.ItemSubstitutionResponse;
import com.nextuple.item.persistence.domain.ItemSubstitutionDomainDto;
import com.nextuple.item.persistence.service.ItemSubstitutionPersistenceService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemSubstitutionService {
  private final ItemSubstitutionPersistenceService itemSubstitutionPersistenceService;

  public static final ItemSubstitutionMapper INSTANCE =
      Mappers.getMapper(ItemSubstitutionMapper.class);

  @Transactional
  public ItemSubstitutionResponse upsertItemSubstitution(
      @Valid UpsertItemSubstitutionRequest upsertItemSubstitutionRequest) {
    log.debug("Upserting item substitution: {}", upsertItemSubstitutionRequest);

    // If priority is not provided, default to 1
    if (upsertItemSubstitutionRequest.getPriority() == null) {
      upsertItemSubstitutionRequest.setPriority(1);
    }

    ItemSubstitutionDomainDto itemSubstitutionDomainDto =
        INSTANCE.upsertRequestToDomainDto(upsertItemSubstitutionRequest);

    ItemSubstitutionDomainDto savedEntity =
        itemSubstitutionPersistenceService.save(itemSubstitutionDomainDto);

    return INSTANCE.entityToDomainDto(savedEntity);
  }

  public List<ItemSubstitutionResponse> getItemSubstitution(String primaryItemId, String primaryUom)
      throws CommonServiceException {
    log.debug(
        "Getting item substitution for primaryItemId: {}, primaryUom: {}",
        primaryItemId,
        primaryUom);

    List<ItemSubstitutionDomainDto> entity =
        itemSubstitutionPersistenceService.findByPrimaryItemIdAndPrimaryUom(
            primaryItemId, primaryUom);

    return INSTANCE.domainDtoToResponse(entity);
  }

  @Transactional
  public void deleteItemSubstitution(DeleteItemSubstitutionRequest deleteItemSubstitutionRequest)
      throws CommonServiceException {
    log.debug("Deleting item substitution for request: {}", deleteItemSubstitutionRequest);

    Optional<ItemSubstitutionDomainDto> dtoOptional =
        itemSubstitutionPersistenceService
            .findByPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
                deleteItemSubstitutionRequest.getPrimaryItemId(),
                deleteItemSubstitutionRequest.getPrimaryUom(),
                deleteItemSubstitutionRequest.getAlternateItemId(),
                deleteItemSubstitutionRequest.getAlternateUom());

    if (dtoOptional.isEmpty()) {
      log.debug(
          "Item substitution not found for primaryItemId: {}, primaryUom: {}, alternateItemId: {}, alternateUom: {}",
          deleteItemSubstitutionRequest.getPrimaryItemId(),
          deleteItemSubstitutionRequest.getPrimaryUom(),
          deleteItemSubstitutionRequest.getAlternateItemId(),
          deleteItemSubstitutionRequest.getAlternateUom());
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format(
              "Item substitution not found for primaryItemId: %s, primaryUom: %s, alternateItemId: %s, alternateUom: %s",
              deleteItemSubstitutionRequest.getPrimaryItemId(),
              deleteItemSubstitutionRequest.getPrimaryUom(),
              deleteItemSubstitutionRequest.getAlternateItemId(),
              deleteItemSubstitutionRequest.getAlternateUom()));
    }

    itemSubstitutionPersistenceService.delete(dtoOptional.get());
  }
}
