/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.persistence.service.impl;

import static com.nextuple.postal.code.timezone.persistence.service.impl.TestUtil.COUNTRY;
import static com.nextuple.postal.code.timezone.persistence.service.impl.TestUtil.ORG_ID;
import static com.nextuple.postal.code.timezone.persistence.service.impl.TestUtil.POSTAL_CODE_PREFIX_2;
import static com.nextuple.postal.code.timezone.persistence.service.impl.TestUtil.STATE;
import static com.nextuple.postal.code.timezone.persistence.service.impl.TestUtil.ZIP_CODE;
import static com.nextuple.postal.code.timezone.persistence.service.impl.TestUtil.ZIP_CODE_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeDomainDto;
import com.nextuple.postal.code.timezone.persistence.entity.PostalCodeEntity;
import com.nextuple.postal.code.timezone.persistence.mapper.PostalCodeEntityMapper;
import com.nextuple.postal.code.timezone.persistence.repository.PostalCodeRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;

class PostalCodePersistenceServiceImplTest {

  @InjectMocks private PostalCodePersistenceServiceImpl postalCodePersistenceService;

  @Mock private PostalCodeRepository postalCodeRepository;

  @Mock private PostalCodeEntityMapper postalCodeEntityMapper;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(postalCodePersistenceService, "repository", postalCodeRepository);
    ReflectionTestUtils.setField(postalCodePersistenceService, "mapper", postalCodeEntityMapper);
  }

  @Test
  @DisplayName("Save Postal Code Test: Happy Path")
  void savePostalCodeTest() throws PromiseEngineException {
    PostalCodeEntity postalCodeEntity = testUtil.getPostalCodeEntity();
    PostalCodeDomainDto postalCodeDomainDto = testUtil.getPostalCodeDomainDto();
    when(postalCodeEntityMapper.toEntity(postalCodeDomainDto)).thenReturn(postalCodeEntity);
    when(postalCodeEntityMapper.toDomain(postalCodeEntity)).thenReturn(postalCodeDomainDto);
    when(postalCodeRepository.save(any(PostalCodeEntity.class))).thenReturn(postalCodeEntity);
    PostalCodeDomainDto received_postalCodeEntityResponse =
        postalCodePersistenceService.savePostalCode(postalCodeDomainDto);
    assertEquals(postalCodeDomainDto, received_postalCodeEntityResponse);
    verify(postalCodeRepository, times(1)).save(postalCodeEntity);
  }

  @Test
  @DisplayName("Save Postal Code Test: Exception")
  void savePostalCodeExceptionTest() {
    PostalCodeEntity postalCodeEntity = testUtil.getPostalCodeEntity();
    PostalCodeDomainDto postalCodeDomainDto = testUtil.getPostalCodeDomainDto();
    when(postalCodeEntityMapper.toEntity(postalCodeDomainDto)).thenReturn(postalCodeEntity);
    when(postalCodeRepository.save(any(PostalCodeEntity.class))).thenThrow(RuntimeException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodePersistenceService.savePostalCode(postalCodeDomainDto);
        });
    verify(postalCodeRepository, times(1)).save(postalCodeEntity);
  }

  @Test
  @DisplayName("Get Postal Code Test: Happy Path")
  void getPostalCodeTest() throws PromiseEngineException {
    PostalCodeEntity postalCodeEntity = testUtil.getPostalCodeEntity();
    PostalCodeDomainDto postalCodeDomainDto = testUtil.getPostalCodeDomainDto();
    when(postalCodeEntityMapper.toDomain(any(PostalCodeEntity.class)))
        .thenReturn(postalCodeDomainDto);
    when(postalCodeRepository.findById(any())).thenReturn(Optional.ofNullable(postalCodeEntity));

    Optional<PostalCodeDomainDto> receivedEntityResponse =
        postalCodePersistenceService.fetchPostalCode(ORG_ID, ZIP_CODE);
    assertEquals(postalCodeDomainDto, receivedEntityResponse.get());
    verify(postalCodeRepository, times(1)).findById(any());
  }

  @Test
  @DisplayName("Get Postal Code Test: Exception")
  void getPostalCodeExceptionTest() {
    when(postalCodeRepository.findById(any())).thenThrow(RuntimeException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodePersistenceService.fetchPostalCode(ORG_ID, ZIP_CODE);
        });
    verify(postalCodeRepository, times(1)).findById(any());
  }

  @Test
  @DisplayName("Delete Postal Code Test: Happy Path")
  void deletePostalCodeTest() throws PromiseEngineException {
    PostalCodeEntity postalCodeEntity = testUtil.getPostalCodeEntity();
    doNothing().when(postalCodeRepository).delete(any(PostalCodeEntity.class));
    when(postalCodeEntityMapper.toEntity(any(PostalCodeDomainDto.class)))
        .thenReturn(postalCodeEntity);

    postalCodePersistenceService.deletePostalCodeEntity(testUtil.getPostalCodeDomainDto());
    verify(postalCodeRepository, times(1)).delete(postalCodeEntity);
  }

  @Test
  @DisplayName("Delete Postal Code Test: Exception")
  void deletePostalCodeExceptionTest() throws PromiseEngineException {
    PostalCodeEntity postalCodeEntity = testUtil.getPostalCodeEntity();
    when(postalCodeEntityMapper.toEntity(any(PostalCodeDomainDto.class)))
        .thenReturn(postalCodeEntity);
    PostalCodeDomainDto postalCodeDomainDto = testUtil.getPostalCodeDomainDto();
    doThrow(new RuntimeException("error"))
        .when(postalCodeRepository)
        .delete(any(PostalCodeEntity.class));
    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodePersistenceService.deletePostalCodeEntity(postalCodeDomainDto);
        });
    verify(postalCodeRepository, times(1)).delete(any(PostalCodeEntity.class));
  }

  @Test
  @DisplayName("Get Postal Code Prefix Test: Happy Path")
  void getPostalCodePrefixTest() throws PromiseEngineException {
    List<PostalCodeEntity> postalCodeEntityList = testUtil.getPostalCodeEntityList();
    List<PostalCodeDomainDto> postalCodeDomainDtoList = testUtil.getPostalCodeDomainDtoList();
    when(postalCodeRepository.findByOrgIdAndZipCodePrefix(anyString(), anyString()))
        .thenReturn(postalCodeEntityList);
    when(postalCodeEntityMapper.toDomain(anyList())).thenReturn(postalCodeDomainDtoList);
    List<PostalCodeDomainDto> receivedEntityResponse =
        postalCodePersistenceService.fetchPostalCodeList(ORG_ID, ZIP_CODE_PREFIX);
    assertEquals(postalCodeDomainDtoList.get(0), receivedEntityResponse.get(0));
  }

  @Test
  @DisplayName("Get Postal Code Prefix Test: Exception")
  void getPostalCodePrefixExceptionTest() {
    when(postalCodeRepository.findByOrgIdAndZipCodePrefix(anyString(), anyString()))
        .thenThrow(RuntimeException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodePersistenceService.fetchPostalCodeList(ORG_ID, ZIP_CODE_PREFIX);
        });
  }

  @Test
  @DisplayName("Get Postal Code Prefix For OrgId And State Test: Happy Path")
  void getPostalCodePrefixForOrgIdAndState() throws PromiseEngineException {
    when(postalCodeRepository.findByOrgIdAndState(anyString(), anyString()))
        .thenReturn(List.of(ZIP_CODE_PREFIX, POSTAL_CODE_PREFIX_2));

    List<String> postalCodePrefixList =
        postalCodePersistenceService.getPostalCodePrefixForOrgIdAndState(ORG_ID, STATE);
    Assertions.assertFalse(CollectionUtils.isEmpty(postalCodePrefixList));
    verify(postalCodeRepository, times(1)).findByOrgIdAndState(anyString(), anyString());
  }

  @Test
  @DisplayName("Get Postal Code Prefix For OrgId And State Test: Exception")
  void getPostalCodePrefixForOrgIdAndStateException() throws PromiseEngineException {
    when(postalCodeRepository.findByOrgIdAndState(anyString(), anyString()))
        .thenThrow(new RuntimeException("Error while fetching postal code prefix list"));

    Exception exception =
        Assertions.assertThrows(
            PromiseEngineException.class,
            () -> postalCodePersistenceService.getPostalCodePrefixForOrgIdAndState(ORG_ID, STATE));
    Assertions.assertNotNull(exception);
    verify(postalCodeRepository, times(1)).findByOrgIdAndState(anyString(), anyString());
  }

  @Test
  @DisplayName("Get Postal Code Prefix For OrgId And Country Test: Happy Path")
  void getPostCodeTimeZoneByOrgIdAndCountry() throws PromiseEngineException {
    when(postalCodeRepository.findByOrgIdAndCountry(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPostalCodeEntity()));
    when(postalCodeEntityMapper.toDomain(anyList()))
        .thenReturn(testUtil.getPostalCodeDomainDtoList());
    List<PostalCodeDomainDto> postalCodePrefixList =
        postalCodePersistenceService.getPostCodeTimeZoneByOrgIdAndCountry(ORG_ID, COUNTRY);
    Assertions.assertFalse(CollectionUtils.isEmpty(postalCodePrefixList));
    verify(postalCodeRepository, times(1)).findByOrgIdAndCountry(anyString(), anyString());
  }

  @Test
  @DisplayName("Get Postal Code Prefix For OrgId And Country Test: Exception")
  void getPostCodeTimeZoneByOrgIdAndCountryException() {
    when(postalCodeRepository.findByOrgIdAndCountry(anyString(), anyString()))
        .thenThrow(new RuntimeException("Error while fetching postal code prefix list"));

    Exception exception =
        Assertions.assertThrows(
            PromiseEngineException.class,
            () ->
                postalCodePersistenceService.getPostCodeTimeZoneByOrgIdAndCountry(ORG_ID, COUNTRY));
    Assertions.assertNotNull(exception);
    verify(postalCodeRepository, times(1)).findByOrgIdAndCountry(anyString(), anyString());
  }

  @Test
  @DisplayName("Get Records For OrgId")
  void getRecordsForOrgId() {
    when(postalCodeRepository.findRecordsByOrgId(anyString()))
        .thenReturn(testUtil.getMarketRegion());

    Assertions.assertDoesNotThrow(() -> postalCodeRepository.findRecordsByOrgId(ORG_ID));
    verify(postalCodeRepository, times(1)).findRecordsByOrgId(anyString());
  }

  @Test
  @DisplayName("Get Records For OrgId Exception")
  void getRecordsForOrgIdException() {
    when(postalCodeRepository.findRecordsByOrgId(anyString())).thenThrow(new RuntimeException());

    Assertions.assertThrows(
        RuntimeException.class, () -> postalCodeRepository.findRecordsByOrgId(ORG_ID));
    verify(postalCodeRepository, times(1)).findRecordsByOrgId(anyString());
  }

  @Test
  @DisplayName("Get Postal Code For OrgId Test")
  void getPostalCodeForOrgIdTest() throws PromiseEngineException {
    PostalCodeEntity postalCodeEntity = testUtil.getPostalCodeEntity();
    when(postalCodeRepository.findByOrgIdOrderByStateAscZipCodePrefixAsc(anyString()))
        .thenReturn(List.of(postalCodeEntity));
    when(postalCodeEntityMapper.toDomain(anyList()))
        .thenReturn(testUtil.getPostalCodeDomainDtoList());
    List<PostalCodeDomainDto> postalCodeEntities =
        postalCodePersistenceService.getPostalCodeForOrgId(ORG_ID);
    assertEquals(1, postalCodeEntities.size());
    assertEquals(postalCodeEntity.getState(), postalCodeEntities.get(0).getState());
    verify(postalCodeRepository, times(1)).findByOrgIdOrderByStateAscZipCodePrefixAsc(anyString());
  }

  @Test
  @DisplayName("Get Postal Code For OrgId Exception Test")
  void getPostalCodeForOrgIdExceptionTest() {
    when(postalCodeRepository.findByOrgIdOrderByStateAscZipCodePrefixAsc(anyString()))
        .thenThrow(RuntimeException.class);

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              postalCodePersistenceService.getPostalCodeForOrgId(ORG_ID);
            });

    assertEquals("Zip Code not found for a given orgId.", ex.getMessage());
    verify(postalCodeRepository, times(1)).findByOrgIdOrderByStateAscZipCodePrefixAsc(anyString());
  }
}
