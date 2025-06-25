/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.substitution.consumer.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.item.domain.feign.ItemSubstitutionFeign;
import com.nextuple.item.domain.outbound.ItemSubstitutionResponse;
import com.nextuple.item.persistence.domain.ItemSubstitutionDomainDto;
import com.nextuple.item.persistence.service.ItemSubstitutionPersistenceService;
import com.nextuple.item.substitution.consumer.dto.ItemSubstitutionFeedDto;
import com.nextuple.item.substitution.consumer.mapper.ItemSubstitutionBatchMapper;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.service.BatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemSubstitutionBatchServiceImpl extends BatchService<ItemSubstitutionFeedDto> {
  private final ItemSubstitutionFeign itemSubstitutionFeign;
  private final ItemSubstitutionPersistenceService itemSubstitutionPersistenceService;
  private final TypeReference<BatchRequest<ItemSubstitutionFeedDto>> itemSubstitutionTypeReference =
      new TypeReference<>() {};
  public static final ItemSubstitutionBatchMapper INSTANCE =
      Mappers.getMapper(ItemSubstitutionBatchMapper.class);

  @Override
  public TaskInformation getTaskInformation() {
    return TaskInformation.ITEM_SUBSTITUTION_FEED;
  }

  @Override
  public TypeReference<BatchRequest<ItemSubstitutionFeedDto>> getTypeReference() {
    return itemSubstitutionTypeReference;
  }

  @Override
  public String createRecordImpl(ItemSubstitutionFeedDto payload) {
    BaseResponse<ItemSubstitutionResponse> responseBody =
        itemSubstitutionFeign
            .upsertItemSubstitution(INSTANCE.toItemSubstitutionUpsertRequest(payload))
            .getBody();
    return Objects.nonNull(responseBody)
        ? responseBody.getMessage()
        : "Failure in creating the item substitute";
  }

  @Override
  public String updateRecordImpl(ItemSubstitutionFeedDto payload) throws CommonServiceException {
    BaseResponse<ItemSubstitutionResponse> responseBody =
        itemSubstitutionFeign
            .upsertItemSubstitution(INSTANCE.toItemSubstitutionUpsertRequest(payload))
            .getBody();
    return Objects.nonNull(responseBody)
        ? responseBody.getMessage()
        : "Failure in updating the item substitute";
  }

  @Override
  public String deleteRecordImpl(ItemSubstitutionFeedDto payload) {
    BaseResponse<Void> responseBody =
        itemSubstitutionFeign
            .deleteItemSubstitution(INSTANCE.toItemSubstitutionDeleteRequest(payload))
            .getBody();
    return Objects.nonNull(responseBody)
        ? responseBody.getMessage()
        : "Failure in deleting the item substitute";
  }

  @Override
  public void checkForOutdatedRecord(
      BatchRequest<ItemSubstitutionFeedDto> itemSubstitutionBatchRequest)
      throws CommonServiceException {
    ItemSubstitutionFeedDto itemSubstitutionFeedDto = itemSubstitutionBatchRequest.getPayload();
    String primaryItemId = itemSubstitutionFeedDto.getPrimaryItemId();
    String orgId = itemSubstitutionFeedDto.getOrgId();
    String primaryUom = itemSubstitutionFeedDto.getPrimaryUom();
    String alertnateItemId = itemSubstitutionFeedDto.getAlternateItemId();
    String alternateUom = itemSubstitutionFeedDto.getAlternateUom();
    if (Objects.nonNull(primaryItemId)
        && Objects.nonNull(primaryUom)
        && Objects.nonNull(orgId)
        && Objects.nonNull(alternateUom)
        && Objects.nonNull(alertnateItemId)) {
      Optional<ItemSubstitutionDomainDto> itemSubstitutionDomainDto =
          itemSubstitutionPersistenceService
              .findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
                  orgId, primaryItemId, primaryUom, alertnateItemId, alternateUom);
      if (checkForBatchRequestExpired(itemSubstitutionBatchRequest, itemSubstitutionDomainDto)) {
        throwExceptionForOutdatedRecords(itemSubstitutionBatchRequest, itemSubstitutionDomainDto);
      }
    }
  }

  private static void throwExceptionForOutdatedRecords(
      BatchRequest<ItemSubstitutionFeedDto> itemSubstitutionBatchRequest,
      Optional<ItemSubstitutionDomainDto> itemSubstitutionDomainDto)
      throws CommonServiceException {
    if (itemSubstitutionDomainDto.isPresent()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          "receivedTimestamp",
          FieldError.builder()
              .rejectedValue(itemSubstitutionBatchRequest.getReceivedTimestamp())
              .build());
      errorMap.put(
          "lastUpdatedTimestamp",
          FieldError.builder()
              .rejectedValue(itemSubstitutionDomainDto.get().getLastModifiedDate())
              .build());
      throw new CommonServiceException(
          "Can't process the record as it's outdated", HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  private static boolean checkForBatchRequestExpired(
      BatchRequest<ItemSubstitutionFeedDto> itemSubstitutionBatchRequest,
      Optional<ItemSubstitutionDomainDto> itemSubstitutionDomainDto) {
    return itemSubstitutionDomainDto.isPresent()
        && (itemSubstitutionDomainDto
            .get()
            .getLastModifiedDate()
            .after(itemSubstitutionBatchRequest.getReceivedTimestamp()));
  }
}
