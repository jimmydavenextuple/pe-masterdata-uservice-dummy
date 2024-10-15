/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.persistence.service.impl;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.item.persistence.domain.ItemBufferDomainDto;
import com.nextuple.item.persistence.domain.key.ItemBufferDomainKey;
import com.nextuple.item.persistence.entity.ItemBufferEntity;
import com.nextuple.item.persistence.entity.key.ItemBufferKey;
import com.nextuple.item.persistence.mapper.ItemBufferEntityMapper;
import com.nextuple.item.persistence.repository.ItemBufferRepository;
import com.nextuple.item.persistence.service.ItemBufferPersistenceService;
import com.nextuple.postgres.service.CommonPersistenceService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemBufferPersistenceServiceImpl
    extends CommonPersistenceService<
        ItemBufferDomainDto,
        ItemBufferDomainKey,
        ItemBufferEntity,
        ItemBufferKey,
        ItemBufferRepository,
        ItemBufferEntityMapper>
    implements ItemBufferPersistenceService {

  private static final String ERROR_WHILE_SAVING = "Error while saving the item buffer.";
  private static final String ERROR_WHILE_FINDING = "Error while finding the item buffer.";
  private static final String ERROR_WHILE_DELETING = "Error while deleting the item buffer.";

  @Override
  public ItemBufferDomainDto saveItemBuffer(ItemBufferDomainDto itemBufferDomainDto)
      throws CommonServiceException {
    try {
      return this.save(itemBufferDomainDto);
    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_SAVING);
      throw new CommonServiceException(
          ERROR_WHILE_SAVING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1991, null);
    }
  }

  @Override
  public List<ItemBufferDomainDto> findByItemIdAndOrgIdAndUom(
      String itemId, String orgId, String uom) throws CommonServiceException {
    try {
      return getMapper().toDomain(getRepository().findByItemIdAndOrgIdAndUom(itemId, orgId, uom));
    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_FINDING);
      throw new CommonServiceException(
          ERROR_WHILE_FINDING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1992, null);
    }
  }

  @Override
  public Optional<ItemBufferDomainDto> findByItemIdAndOrgIdAndUomAndBufferStartDateAndBufferEndDate(
      String itemId, String orgId, String uom, Date bufferStartDate, Date bufferEndDate)
      throws CommonServiceException {
    try {
      return getRepository()
          .findByItemIdAndOrgIdAndUomAndBufferStartDateAndBufferEndDate(
              itemId, orgId, uom, bufferStartDate, bufferEndDate)
          .map(getMapper()::toDomain);
    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_FINDING);
      throw new CommonServiceException(
          ERROR_WHILE_FINDING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1993, null);
    }
  }

  @Override
  public Optional<ItemBufferDomainDto> findItemBuffer(ItemBufferDomainDto itemBufferDomainDto)
      throws CommonServiceException {
    try {
      return getRepository()
          .findByItemIdAndOrgIdAndUomAndAndBufferStartDateAndBufferEndDateAndBufferHours(
              itemBufferDomainDto.getItemId(),
              itemBufferDomainDto.getOrgId(),
              itemBufferDomainDto.getUom(),
              itemBufferDomainDto.getBufferStartDate(),
              itemBufferDomainDto.getBufferEndDate(),
              itemBufferDomainDto.getBufferHours())
          .map(getMapper()::toDomain);
    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_FINDING);
      throw new CommonServiceException(
          ERROR_WHILE_FINDING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1994, null);
    }
  }

  @Override
  public Optional<ItemBufferDomainDto> findItemBufferByOrgIdAndId(String orgId, Long id)
      throws CommonServiceException {
    try {
      return getRepository().findByOrgIdAndId(orgId, id).map(getMapper()::toDomain);
    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_FINDING);
      throw new CommonServiceException(
          ERROR_WHILE_FINDING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1995, null);
    }
  }

  @Override
  public void deleteItemBuffer(ItemBufferDomainDto itemBufferDomainDto)
      throws CommonServiceException {
    try {
      this.delete(itemBufferDomainDto);
    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_DELETING);
      throw new CommonServiceException(
          ERROR_WHILE_DELETING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1996, null);
    }
  }

  @Override
  public Page<ItemBufferDomainDto> findByOrgId(String orgId, Pageable pageable)
      throws CommonServiceException {
    try {
      Page<ItemBufferEntity> itemBufferEntities =
          getRepository().findDistinctItemIdAndOrgIdAndUomByOrgId(orgId, pageable);
      List<ItemBufferDomainDto> itemBufferDomainDtos =
          getMapper().toDomain(itemBufferEntities.getContent());
      return new PageImpl<>(itemBufferDomainDtos, pageable, itemBufferEntities.getTotalElements());
    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_FINDING);
      throw new CommonServiceException(
          ERROR_WHILE_FINDING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1997, null);
    }
  }

  @Override
  public List<ItemBufferDomainDto> findItemBuffersListByItemIdAndOrgIdAndUom(
      String itemId, String orgId, String uom) throws CommonServiceException {
    try {
      List<ItemBufferEntity> itemBufferEntities =
          getRepository().findByItemIdAndOrgIdAndUom(itemId, orgId, uom);
      return getMapper().toDomain(itemBufferEntities);
    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_FINDING);
      throw new CommonServiceException(
          ERROR_WHILE_FINDING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1998, null);
    }
  }

  @Override
  public List<ItemBufferDomainDto> findItemBuffersListByItemIdsAndOrgIdAndPromisingEngineDate(
      List<String> itemIdList, String orgId, Date promisingEngineDate)
      throws CommonServiceException {
    try {
      List<ItemBufferEntity> itemBufferEntities =
          getRepository()
              .findByItemIdInAndOrgIdAndBufferStartDateBeforeAndBufferEndDateAfter(
                  itemIdList, orgId, promisingEngineDate, promisingEngineDate);
      return getMapper().toDomain(itemBufferEntities);

    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_FINDING);
      throw new CommonServiceException(
          ERROR_WHILE_FINDING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1999, null);
    }
  }

  @Override
  public Page<ItemBufferDomainDto> findItemBufferByItemIdsAndOrgId(
      String orgId, List<String> itemIds, Pageable pageable) throws CommonServiceException {
    try {
      Page<ItemBufferEntity> itemBufferEntities =
          getRepository()
              .findDistinctItemIdAndOrgIdAndUomByItemIdsAndOrgId(itemIds, orgId, pageable);
      List<ItemBufferDomainDto> itemBufferDomainDtos =
          getMapper().toDomain(itemBufferEntities.getContent());
      return new PageImpl<>(itemBufferDomainDtos, pageable, itemBufferEntities.getTotalElements());
    } catch (Exception e) {
      log.error(String.valueOf(e), ERROR_WHILE_FINDING);
      throw new CommonServiceException(
          ERROR_WHILE_FINDING, HttpStatus.INTERNAL_SERVER_ERROR, 0x1997, null);
    }
  }
}
