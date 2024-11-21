/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.persistence.service.impl;

import static com.nextuple.postal.code.timezone.persistence.service.impl.TestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeTimezoneDomainDto;
import com.nextuple.postal.code.timezone.persistence.entity.PostalCodeTimezoneEntity;
import com.nextuple.postal.code.timezone.persistence.mapper.PostalCodeTimezoneEntityMapper;
import com.nextuple.postal.code.timezone.persistence.repository.PostalCodeTimezoneRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class PostalCodeTimezonePersistenceServiceImplTest {

  @InjectMocks
  private PostalCodeTimezonePersistenceServiceImpl postalCodeTimezonePersistenceService;

  @Mock private PostalCodeTimezoneRepository postalCodeTimezoneRepository;

  @Mock private PostalCodeTimezoneEntityMapper postalCodeTimezoneEntityMapper;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(
        postalCodeTimezonePersistenceService, "repository", postalCodeTimezoneRepository);
    ReflectionTestUtils.setField(
        postalCodeTimezonePersistenceService, "mapper", postalCodeTimezoneEntityMapper);
  }

  @Test
  @DisplayName("Save Postal Code Timezone Test: Happy Path")
  void savePostalCodeTimezoneTest() throws PromiseEngineException {
    PostalCodeTimezoneEntity postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();
    PostalCodeTimezoneDomainDto postalCodeTimezoneDomainDto =
        testUtil.getPostalCodeTimezoneDomainDto();
    when(postalCodeTimezoneEntityMapper.toEntity(any(PostalCodeTimezoneDomainDto.class)))
        .thenReturn(postalCodeTimezoneEntity);
    when(postalCodeTimezoneEntityMapper.toDomain(any(PostalCodeTimezoneEntity.class)))
        .thenReturn(postalCodeTimezoneDomainDto);
    when(postalCodeTimezoneRepository.save(any(PostalCodeTimezoneEntity.class)))
        .thenReturn(postalCodeTimezoneEntity);
    PostalCodeTimezoneDomainDto received_postalCodeTimezoneResponse =
        postalCodeTimezonePersistenceService.savePostalCodeTimezone(postalCodeTimezoneDomainDto);
    assertEquals(postalCodeTimezoneDomainDto, received_postalCodeTimezoneResponse);
    verify(postalCodeTimezoneRepository, times(1)).save(postalCodeTimezoneEntity);
  }

  @Test
  @DisplayName("Save Postal Code Timezone Test: Exception Path")
  void savePostalCodeTimezoneExceptionTest() {
    PostalCodeTimezoneEntity postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();
    PostalCodeTimezoneDomainDto postalCodeTimezoneDomainDto =
        testUtil.getPostalCodeTimezoneDomainDto();
    when(postalCodeTimezoneEntityMapper.toEntity(any(PostalCodeTimezoneDomainDto.class)))
        .thenReturn(postalCodeTimezoneEntity);
    when(postalCodeTimezoneRepository.save(any(PostalCodeTimezoneEntity.class)))
        .thenThrow(RuntimeException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeTimezonePersistenceService.savePostalCodeTimezone(postalCodeTimezoneDomainDto);
        });
    verify(postalCodeTimezoneRepository, times(1)).save(postalCodeTimezoneEntity);
  }

  @Test
  @DisplayName("Get Postal Code Timezone Test: Happy Path")
  void getPostalCodeTimezoneTest() throws PromiseEngineException {
    PostalCodeTimezoneEntity postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();
    when(postalCodeTimezoneRepository.findByOrgIdAndZipCodePrefix(anyString(), anyString()))
        .thenReturn(postalCodeTimezoneEntity);
    when(postalCodeTimezoneEntityMapper.toDomain(any(PostalCodeTimezoneEntity.class)))
        .thenReturn(testUtil.getPostalCodeTimezoneDomainDto());
    PostalCodeTimezoneDomainDto received_entityResponse =
        postalCodeTimezonePersistenceService.getPostalCodeTimezone(ORG_ID, ZIP_CODE_PREFIX);
    assertEquals(ORG_ID, received_entityResponse.getOrgId());
    verify(postalCodeTimezoneRepository, times(1))
        .findByOrgIdAndZipCodePrefix(anyString(), anyString());
  }

  @Test
  @DisplayName("Get Postal Code Timezone Test: Exception Path")
  void getPostalCodeTimezoneExceptionTest() {
    when(postalCodeTimezoneRepository.findByOrgIdAndZipCodePrefix(anyString(), anyString()))
        .thenThrow(RuntimeException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeTimezonePersistenceService.getPostalCodeTimezone(ORG_ID, ZIP_CODE_PREFIX);
        });
    verify(postalCodeTimezoneRepository, times(1))
        .findByOrgIdAndZipCodePrefix(anyString(), anyString());
  }

  @Test
  @DisplayName("Delete Postal Code Timezone Test: Happy Path")
  void deletePostalCodeTimezoneTest() throws PromiseEngineException {
    PostalCodeTimezoneEntity postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();
    PostalCodeTimezoneDomainDto postalCodeTimezoneDomainDto =
        testUtil.getPostalCodeTimezoneDomainDto();
    when(postalCodeTimezoneEntityMapper.toEntity(any(PostalCodeTimezoneDomainDto.class)))
        .thenReturn(postalCodeTimezoneEntity);
    when(postalCodeTimezoneEntityMapper.toDomain(any(PostalCodeTimezoneEntity.class)))
        .thenReturn(postalCodeTimezoneDomainDto);
    when(postalCodeTimezoneRepository.findByOrgIdAndZipCodePrefix(anyString(), anyString()))
        .thenReturn(postalCodeTimezoneEntity);
    PostalCodeTimezoneDomainDto received_entityResponse =
        postalCodeTimezonePersistenceService.deletePostalCodeTimezone(postalCodeTimezoneDomainDto);
    assertEquals(ORG_ID, received_entityResponse.getOrgId());
    verify(postalCodeTimezoneRepository, times(1)).delete(postalCodeTimezoneEntity);
  }

  @Test
  @DisplayName("Delete Postal Code Timezone Test: Exception Path")
  void getPostalCodeTimezoneForOrgIdTest() throws PromiseEngineException {
    List<PostalCodeTimezoneEntity> postalCodeTimezoneEntityList =
        testUtil.getPostalCodeTimezoneEntityList();
    List<PostalCodeTimezoneDomainDto> postalCodeTimezoneDomainDtoList =
        testUtil.getPostalCodeTimezoneDomainDtoList();
    when(postalCodeTimezoneRepository.findByOrgId(anyString()))
        .thenReturn(postalCodeTimezoneEntityList);

    when(postalCodeTimezoneEntityMapper.toDomain(anyList()))
        .thenReturn(postalCodeTimezoneDomainDtoList);

    List<PostalCodeTimezoneDomainDto> postalCodeTimezoneEntities =
        postalCodeTimezonePersistenceService.getPostalCodeTimezoneForOrgId(ORG_ID);

    assertEquals(1, postalCodeTimezoneEntities.size());
    assertEquals(
        postalCodeTimezoneDomainDtoList.get(0).getState(),
        postalCodeTimezoneEntities.get(0).getState());
    verify(postalCodeTimezoneRepository, times(1)).findByOrgId(anyString());
  }

  @Test
  @DisplayName("Get Postal Code Timezone Test: Exception Path")
  void getPostalCodeTimezoneForOrgIdExceptionTest() {
    when(postalCodeTimezoneRepository.findByOrgId(anyString())).thenThrow(RuntimeException.class);

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              postalCodeTimezonePersistenceService.getPostalCodeTimezoneForOrgId(ORG_ID);
            });

    assertEquals("Zip Code Timezone not found for a given orgId.", ex.getMessage());
    verify(postalCodeTimezoneRepository, times(1)).findByOrgId(anyString());
  }
}
