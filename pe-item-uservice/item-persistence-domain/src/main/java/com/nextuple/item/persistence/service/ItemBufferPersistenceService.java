/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.persistence.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.item.persistence.domain.ItemBufferDomainDto;
import com.nextuple.item.persistence.domain.key.ItemBufferDomainKey;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemBufferPersistenceService
    extends DomainPersistenceService<ItemBufferDomainDto, ItemBufferDomainKey> {

  ItemBufferDomainDto saveItemBuffer(ItemBufferDomainDto itemBufferDomainDto)
      throws CommonServiceException;

  List<ItemBufferDomainDto> findByItemIdAndOrgIdAndUom(String itemId, String orgId, String uom)
      throws CommonServiceException;

  Optional<ItemBufferDomainDto> findByItemIdAndOrgIdAndUomAndBufferStartDateAndBufferEndDate(
      String itemId, String orgId, String uom, Date bufferStartDate, Date bufferEndDate)
      throws CommonServiceException;

  Optional<ItemBufferDomainDto> findItemBuffer(ItemBufferDomainDto itemBufferDomainDto)
      throws CommonServiceException;

  Optional<ItemBufferDomainDto> findItemBufferByOrgIdAndId(String orgId, Long id)
      throws CommonServiceException;

  void deleteItemBuffer(ItemBufferDomainDto itemBufferDomainDto) throws CommonServiceException;

  List<ItemBufferDomainDto> findItemBuffersListByItemIdAndOrgIdAndUom(
      String itemId, String orgId, String uom) throws CommonServiceException;

  Page<ItemBufferDomainDto> findByOrgId(String orgId, Pageable pageable)
      throws CommonServiceException;

  List<ItemBufferDomainDto> findItemBuffersListByItemIdsAndOrgIdAndPromisingEngineDate(
      List<String> itemIdList, String orgId, Date promisingEngineDate)
      throws CommonServiceException;

  Page<ItemBufferDomainDto> findItemBufferByItemIdsAndOrgId(
      String orgId, List<String> itemIds, Pageable pageable) throws CommonServiceException;
}
