/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.substitution.consumer.consumer;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.item.domain.feign.ItemSubstitutionFeign;
import com.nextuple.item.domain.outbound.ItemSubstitutionResponse;
import com.nextuple.item.persistence.domain.ItemSubstitutionDomainDto;
import com.nextuple.item.persistence.service.ItemSubstitutionPersistenceService;
import com.nextuple.item.substitution.consumer.dto.ItemSubstitutionFeedDto;
import com.nextuple.item.substitution.consumer.impl.ItemSubstitutionBatchServiceImpl;
import com.nextuple.master.data.integration.enums.TaskInformation;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ItemSubstitutionBatchServiceImplTest {

    @InjectMocks
    private ItemSubstitutionBatchServiceImpl itemSubstitutionBatchService;

    @Mock
    private ItemSubstitutionFeign itemSubstitutionFeign;

    @Mock
    private ItemSubstitutionPersistenceService itemSubstitutionPersistenceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTaskInformation() {
        TaskInformation taskInformation = itemSubstitutionBatchService.getTaskInformation();
        assertEquals(TaskInformation.ITEM_SUBSTITUTION_FEED, taskInformation);
    }

    @Test
    void testCreateRecordImpl() {
        ItemSubstitutionFeedDto payload = new ItemSubstitutionFeedDto();
        BaseResponse<ItemSubstitutionResponse> mockResponse = new BaseResponse<>();
        mockResponse.setMessage("Success");
        ResponseEntity<BaseResponse<ItemSubstitutionResponse>> mockResponseEntity = ResponseEntity.ok(mockResponse);

        when(itemSubstitutionFeign.upsertItemSubstitution(any())).thenReturn(mockResponseEntity);

        String result = itemSubstitutionBatchService.createRecordImpl(payload);

        assertEquals("Success", result);
        verify(itemSubstitutionFeign, times(1)).upsertItemSubstitution(any());
    }

    @Test
    void testUpdateRecordImpl() throws CommonServiceException {
        ItemSubstitutionFeedDto payload = new ItemSubstitutionFeedDto();
        BaseResponse<ItemSubstitutionResponse> mockResponse = new BaseResponse<>();
        mockResponse.setMessage("Updated");
        ResponseEntity<BaseResponse<ItemSubstitutionResponse>> mockResponseEntity = ResponseEntity.ok(mockResponse);
        when(itemSubstitutionFeign.upsertItemSubstitution(any())).thenReturn(mockResponseEntity);

        String result = itemSubstitutionBatchService.updateRecordImpl(payload);

        assertEquals("Updated", result);
        verify(itemSubstitutionFeign, times(1)).upsertItemSubstitution(any());
    }

    @Test
    void testDeleteRecordImpl() throws CommonServiceException {
        ItemSubstitutionFeedDto payload = new ItemSubstitutionFeedDto();
        BaseResponse<Void> mockResponse = new BaseResponse<>();
        mockResponse.setMessage("Deleted");
        ResponseEntity<BaseResponse<Void>> mockResponseEntity = ResponseEntity.ok(mockResponse);
        when(itemSubstitutionFeign.deleteItemSubstitution(any())).thenReturn(mockResponseEntity);

        String result = itemSubstitutionBatchService.deleteRecordImpl(payload);

        assertEquals("Deleted", result);
        verify(itemSubstitutionFeign, times(1)).deleteItemSubstitution(any());
    }

    @Test
    void testCheckForOutdatedRecord() throws CommonServiceException {
        BatchRequest<ItemSubstitutionFeedDto> batchRequest = new BatchRequest<>();
        ItemSubstitutionFeedDto payload = new ItemSubstitutionFeedDto();
        payload.setPrimaryItemId("item1");
        payload.setOrgId("org1");
        payload.setPrimaryUom("EA");
        payload.setAlternateItemId("item2");
        payload.setAlternateUom("KG");
        batchRequest.setPayload(payload);
        batchRequest.setReceivedTimestamp(new Date());

        ItemSubstitutionDomainDto domainDto = new ItemSubstitutionDomainDto();
        domainDto.setLastModifiedDate(new Date(System.currentTimeMillis() - 1000));
        Optional<ItemSubstitutionDomainDto> optionalDomainDto = Optional.of(domainDto);

        when(itemSubstitutionPersistenceService.findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
                "org1", "item1", "EA", "item2", "KG")).thenReturn(optionalDomainDto);

        itemSubstitutionBatchService.checkForOutdatedRecord(batchRequest);

        verify(itemSubstitutionPersistenceService, times(1))
                .findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
                        "org1", "item1", "EA", "item2", "KG");
    }

    @Test
    void testCheckForOutdatedRecordThrowsException() {
        BatchRequest<ItemSubstitutionFeedDto> batchRequest = new BatchRequest<>();
        ItemSubstitutionFeedDto payload = new ItemSubstitutionFeedDto();
        payload.setPrimaryItemId("item1");
        payload.setOrgId("org1");
        payload.setPrimaryUom("EA");
        payload.setAlternateItemId("item2");
        payload.setAlternateUom("KG");
        batchRequest.setPayload(payload);
        batchRequest.setReceivedTimestamp(new Date());

        ItemSubstitutionDomainDto domainDto = new ItemSubstitutionDomainDto();
        domainDto.setLastModifiedDate(new Date(System.currentTimeMillis() + 1000));
        Optional<ItemSubstitutionDomainDto> optionalDomainDto = Optional.of(domainDto);

        when(itemSubstitutionPersistenceService.findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
                "org1", "item1", "EA", "item2", "KG")).thenReturn(optionalDomainDto);

        CommonServiceException exception = assertThrows(CommonServiceException.class, () ->
                itemSubstitutionBatchService.checkForOutdatedRecord(batchRequest));

        assertEquals("Can't process the record as it's outdated", exception.getMessage());
        verify(itemSubstitutionPersistenceService, times(1))
                .findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
                        "org1", "item1", "EA", "item2", "KG");
    }
}
