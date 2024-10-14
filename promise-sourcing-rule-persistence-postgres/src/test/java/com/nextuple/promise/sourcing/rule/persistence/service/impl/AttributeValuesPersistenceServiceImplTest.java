/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.promise.sourcing.rule.persistence.domain.AttributeValuesDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.entity.AttributeValuesEntity;
import com.nextuple.promise.sourcing.rule.persistence.mapper.AttributeValuesEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.repository.AttributeValuesRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class AttributeValuesPersistenceServiceImplTest {

  @InjectMocks private AttributeValuesPersistenceServiceImpl attributeValuesPersistenceService;

  @Mock private AttributeValuesRepository attributeValuesRepository;

  @Mock private AttributeValuesEntityMapper attributeValuesEntityMapper;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(
        attributeValuesPersistenceService, "repository", attributeValuesRepository);
    ReflectionTestUtils.setField(
        attributeValuesPersistenceService, "mapper", attributeValuesEntityMapper);
  }

  @Test
  void getAttributeValuesTest() throws PromiseEngineException {
    List<AttributeValuesEntity> attributeValuesEntityList = testUtil.getAttributeValuesEntityList();
    List<AttributeValuesDomainDto> attributeValuesDomainDtoList =
        testUtil.getAttributeValuesDomainDtoList();
    when(attributeValuesRepository.findByNameId(any())).thenReturn(attributeValuesEntityList);
    when(attributeValuesEntityMapper.toDomain(attributeValuesEntityList))
        .thenReturn(attributeValuesDomainDtoList);
    List<AttributeValuesDomainDto> receivedResponse =
        attributeValuesPersistenceService.getAttributeValues(TestUtil.ATTRIBUTE_ID);
    assertEquals(attributeValuesDomainDtoList.get(0), receivedResponse.get(0));
    verify(attributeValuesRepository, times(1)).findByNameId(any());
  }

  @Test
  void getAttributeValuesExceptionTest() {
    when(attributeValuesRepository.findByNameId(any())).thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              attributeValuesPersistenceService.getAttributeValues(TestUtil.ATTRIBUTE_ID);
            });

    assertEquals("Unable to find the attribute values", ex.getMessage());

    verify(attributeValuesRepository, times(1)).findByNameId(any());
  }

  @Test
  void addValueToAttributeTest() throws PromiseEngineException {
    AttributeValuesEntity attributeValuesEntity = testUtil.getAttributeValuesEntity();
    when(attributeValuesRepository.save(any(AttributeValuesEntity.class)))
        .thenReturn(attributeValuesEntity);
    AttributeValuesDomainDto attributeValuesDomainDto = testUtil.getAttributeValuesDomainDto();
    when(attributeValuesEntityMapper.toDomain(attributeValuesEntity))
        .thenReturn(attributeValuesDomainDto);
    when(attributeValuesEntityMapper.toEntity(any(AttributeValuesDomainDto.class)))
        .thenReturn(attributeValuesEntity);
    AttributeValuesDomainDto saved_attributeValuesEntity =
        attributeValuesPersistenceService.addValueToAttribute(
            TestUtil.ATTRIBUTE_ID, TestUtil.ATTRIBUTE_VALUE);
    assertEquals(attributeValuesDomainDto, saved_attributeValuesEntity);
    verify(attributeValuesRepository, times(1)).save(any());
  }

  @Test
  void addValueToAttributeExceptionTest() {
    when(attributeValuesEntityMapper.toEntity(any(AttributeValuesDomainDto.class)))
        .thenReturn(testUtil.getAttributeValuesEntity());
    when(attributeValuesRepository.save(any(AttributeValuesEntity.class)))
        .thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              attributeValuesPersistenceService.addValueToAttribute(
                  TestUtil.ATTRIBUTE_ID, TestUtil.ATTRIBUTE_VALUE);
            });

    assertEquals("Unable to add value to attribute", ex.getMessage());
  }

  @Test
  void deleteValueForAttributeTest() throws PromiseEngineException {
    doNothing().when(attributeValuesRepository).deleteByNameIdAndValue(anyLong(), anyString());

    attributeValuesPersistenceService.deleteValueForAttribute(
        TestUtil.ATTRIBUTE_ID, TestUtil.ATTRIBUTE_VALUE);

    verify(attributeValuesRepository, times(1)).deleteByNameIdAndValue(anyLong(), anyString());
  }

  @Test
  void deleteValueForAttributeExceptionTest() {
    doThrow(new RuntimeException("error"))
        .when(attributeValuesRepository)
        .deleteByNameIdAndValue(anyLong(), anyString());

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              attributeValuesPersistenceService.deleteValueForAttribute(
                  TestUtil.ATTRIBUTE_ID, TestUtil.ATTRIBUTE_VALUE);
            });

    assertEquals("Unable to delete value of attribute", ex.getMessage());
  }

  @Test
  void getAllAttributeValuesTest() throws PromiseEngineException {
    List<AttributeValuesEntity> attributeValuesEntityList = testUtil.getAttributeValuesEntityList();
    when(attributeValuesRepository.findByNameIdIn(any())).thenReturn(attributeValuesEntityList);
    List<AttributeValuesDomainDto> attributeValuesDomainDtoList =
        testUtil.getAttributeValuesDomainDtoList();
    when(attributeValuesEntityMapper.toDomain(attributeValuesEntityList))
        .thenReturn(attributeValuesDomainDtoList);
    List<AttributeValuesDomainDto> receivedResponse =
        attributeValuesPersistenceService.getAllAttributeValues(List.of(TestUtil.ATTRIBUTE_ID));
    assertEquals(attributeValuesDomainDtoList.get(0), receivedResponse.get(0));
    verify(attributeValuesRepository, times(1)).findByNameIdIn(any());
  }

  @Test
  void getAllAttributeValuesExceptionTest() {
    when(attributeValuesRepository.findByNameIdIn(any())).thenThrow(new RuntimeException("error"));

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              attributeValuesPersistenceService.getAllAttributeValues(
                  List.of(TestUtil.ATTRIBUTE_ID));
            });

    assertEquals("Unable to find the attribute values list", ex.getMessage());

    verify(attributeValuesRepository, times(1)).findByNameIdIn(any());
  }
}
