/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.service;

import static com.nextuple.postal.code.timezone.TestUtil.COUNTRY;
import static com.nextuple.postal.code.timezone.TestUtil.ORG_ID;
import static com.nextuple.postal.code.timezone.TestUtil.POSTAL_CODE_PREFIX_2;
import static com.nextuple.postal.code.timezone.TestUtil.STATE;
import static com.nextuple.postal.code.timezone.TestUtil.ZIP_CODE;
import static com.nextuple.postal.code.timezone.TestUtil.ZIP_CODE_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.postal.code.timezone.TestUtil;
import com.nextuple.postal.code.timezone.api.domain.dto.MarketRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodePrefixDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.PostalCodeRequest;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeDomainDto;
import com.nextuple.postal.code.timezone.persistence.service.CustomRegionPersistenceService;
import com.nextuple.postal.code.timezone.persistence.service.PostalCodePersistenceService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.CollectionUtils;

class PostalCodeServiceTest {

  @Mock private PostalCodePersistenceService postalCodePersistenceService;
  @Mock private CustomRegionPersistenceService customRegionPersistenceService;
  @InjectMocks private PostalCodeService postalCodeService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createPostalCodeTest1() throws PromiseEngineException, CommonServiceException {
    PostalCodeDomainDto postalCodeEntity = testUtil.getPostalCodeEntity();
    PostalCodeRequest postalCodeRequest = testUtil.getPostalCodeRequest();
    when(postalCodePersistenceService.savePostalCode(any(PostalCodeDomainDto.class)))
        .thenReturn(postalCodeEntity);

    PostalCodeResponse received_dto = postalCodeService.createPostalCode(postalCodeRequest);
    assertEquals(postalCodeEntity.getOrgId(), received_dto.getOrgId());
    assertEquals(postalCodeEntity.getCustomAttributes(), received_dto.getCustomAttributes());
    verify(postalCodePersistenceService, times(1)).savePostalCode(any(PostalCodeDomainDto.class));
  }

  @Test
  void createPostalCodeTest2() throws PromiseEngineException, CommonServiceException {
    PostalCodeDomainDto postalCodeEntity = testUtil.getPostalCodeEntity();
    PostalCodeRequest postalCodeRequest = testUtil.getPostalCodeRequest();

    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCustomRegionEntity()));
    when(postalCodePersistenceService.savePostalCode(any(PostalCodeDomainDto.class)))
        .thenReturn(postalCodeEntity);

    PostalCodeResponse received_dto = postalCodeService.createPostalCode(postalCodeRequest);
    assertEquals(postalCodeEntity.getOrgId(), received_dto.getOrgId());
    assertEquals(postalCodeEntity.getCustomAttributes(), received_dto.getCustomAttributes());
    verify(postalCodePersistenceService, times(1)).savePostalCode(any(PostalCodeDomainDto.class));
  }

  @Test
  void createPostalCodeExceptionTest() throws PromiseEngineException {
    PostalCodeRequest postalCodeRequest = testUtil.getPostalCodeRequest();
    when(postalCodePersistenceService.fetchPostalCode(anyString(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getPostalCodeEntity()));

    assertThrows(
        CommonServiceException.class,
        () -> {
          postalCodeService.createPostalCode(postalCodeRequest);
        });
    verify(postalCodePersistenceService, times(1)).fetchPostalCode(anyString(), anyString());
  }

  @Test
  void getPostalCodeTest() throws PromiseEngineException, CommonServiceException {
    PostalCodeDomainDto postalCodeEntity = testUtil.getPostalCodeEntity();
    when(postalCodePersistenceService.fetchPostalCode(anyString(), anyString()))
        .thenReturn(Optional.ofNullable(postalCodeEntity));

    PostalCodeResponse postalCodeResponsePojo = postalCodeService.fetchPostalCode(ORG_ID, ZIP_CODE);
    assertEquals(postalCodeResponsePojo.getOrgId(), postalCodeEntity.getOrgId());
    assertEquals(
        postalCodeResponsePojo.getCustomAttributes(), postalCodeEntity.getCustomAttributes());
    verify(postalCodePersistenceService, times(1)).fetchPostalCode(anyString(), anyString());
  }

  @Test
  void getPostalCodeNotFoundTest() throws PromiseEngineException {
    when(postalCodePersistenceService.fetchPostalCode(anyString(), anyString()))
        .thenReturn(Optional.empty());

    assertThrows(
        CommonServiceException.class,
        () -> {
          postalCodeService.fetchPostalCode(ORG_ID, ZIP_CODE);
        });
    verify(postalCodePersistenceService, times(1)).fetchPostalCode(anyString(), anyString());
  }

  @Test
  void updatePostalCodeTest() throws PromiseEngineException, CommonServiceException {
    PostalCodeDomainDto postalCodeEntity = testUtil.getPostalCodeEntity();
    PostalCodeRequest postalCodeRequest = testUtil.getPostalCodeRequest();

    when(postalCodePersistenceService.fetchPostalCode(anyString(), anyString()))
        .thenReturn(Optional.ofNullable(postalCodeEntity));
    when(postalCodePersistenceService.savePostalCode(any(PostalCodeDomainDto.class)))
        .thenReturn(postalCodeEntity);

    PostalCodeResponse postalCodeResponse = postalCodeService.updatePostalCode(postalCodeRequest);
    assertEquals(postalCodeRequest.getCountry(), postalCodeResponse.getCountry());
    assertEquals(postalCodeRequest.getCity(), postalCodeResponse.getCity());
    assertEquals(postalCodeRequest.getCustomAttributes(), postalCodeResponse.getCustomAttributes());
    verify(postalCodePersistenceService, times(1)).fetchPostalCode(anyString(), anyString());
    verify(postalCodePersistenceService, times(1)).savePostalCode(any(PostalCodeDomainDto.class));
  }

  @Test
  void updatePostalCodeExceptionTest() throws PromiseEngineException {
    PostalCodeRequest postalCodeRequest = testUtil.getPostalCodeRequest();
    when(postalCodePersistenceService.fetchPostalCode(anyString(), anyString()))
        .thenReturn(Optional.empty());

    assertThrows(
        CommonServiceException.class,
        () -> {
          postalCodeService.updatePostalCode(postalCodeRequest);
        });
    verify(postalCodePersistenceService, times(1)).fetchPostalCode(anyString(), anyString());
  }

  @Test
  void deletePostalCodeTimezoneTest() throws PromiseEngineException, CommonServiceException {
    PostalCodeDomainDto postalCodeEntity = testUtil.getPostalCodeEntity();

    when(postalCodePersistenceService.fetchPostalCode(anyString(), anyString()))
        .thenReturn(Optional.ofNullable(postalCodeEntity));

    PostalCodeResponse postalCodeResponse =
        postalCodeService.processRemovePostalCode(ORG_ID, ZIP_CODE);
    assertEquals(postalCodeEntity.getOrgId(), postalCodeResponse.getOrgId());
    assertEquals(postalCodeEntity.getCustomAttributes(), postalCodeResponse.getCustomAttributes());
  }

  @Test
  void deletePostalCodeExceptionTest() throws PromiseEngineException {
    PostalCodeRequest postalCodeRequest = testUtil.getPostalCodeRequest();
    when(postalCodePersistenceService.fetchPostalCode(anyString(), anyString()))
        .thenReturn(Optional.empty());
    assertThrows(
        CommonServiceException.class,
        () -> {
          postalCodeService.processRemovePostalCode(
              postalCodeRequest.getOrgId(), postalCodeRequest.getZipCode());
        });
    verify(postalCodePersistenceService, times(1)).fetchPostalCode(anyString(), anyString());
  }

  @Test
  void getPostalCodePrefixTest() throws PromiseEngineException, CommonServiceException {
    PostalCodeDomainDto postalCodeEntity = testUtil.getPostalCodeEntity();
    when(postalCodePersistenceService.fetchPostalCodeList(anyString(), anyString()))
        .thenReturn(List.of(postalCodeEntity));

    List<PostalCodeResponse> postalCodeEntityList =
        postalCodeService.fetchByPostalCodePrefix(ORG_ID, ZIP_CODE_PREFIX);
    assertEquals(postalCodeEntityList.get(0).getOrgId(), postalCodeEntity.getOrgId());
    assertEquals(
        postalCodeEntityList.get(0).getCustomAttributes(), postalCodeEntity.getCustomAttributes());
    verify(postalCodePersistenceService, times(1)).fetchPostalCodeList(anyString(), anyString());
  }

  @Test
  void fetchPostalCodePrefixListTest() throws PromiseEngineException {
    PostalCodeDomainDto postalCodeEntity = testUtil.getPostalCodeEntity();

    when(postalCodePersistenceService.getPostalCodeForOrgId(anyString()))
        .thenReturn(List.of(postalCodeEntity));

    List<PostalCodePrefixDto> postalCodePrefixDtoList =
        postalCodeService.getPostalCodePrefixList(ORG_ID);

    assertEquals(1, postalCodePrefixDtoList.size());
    assertEquals(postalCodeEntity.getState(), postalCodePrefixDtoList.get(0).getState());
    verify(postalCodePersistenceService, times(1)).getPostalCodeForOrgId(anyString());
  }

  @Test
  void getPostalCodePrefixNotFoundTest() throws PromiseEngineException {
    List<PostalCodeDomainDto> postalCodeEntityList = new ArrayList<>();
    when(postalCodePersistenceService.fetchPostalCodeList(anyString(), anyString()))
        .thenReturn(postalCodeEntityList);

    assertThrows(
        CommonServiceException.class,
        () -> {
          postalCodeService.fetchByPostalCodePrefix(ORG_ID, ZIP_CODE_PREFIX);
        });
    verify(postalCodePersistenceService, times(1)).fetchPostalCodeList(anyString(), anyString());
  }

  @Test
  void getCustomRegionIdTestWithException() throws PromiseEngineException {
    when(postalCodePersistenceService.fetchPostalCode(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getPostalCodeEntity()));
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCustomRegionEntity()));
    assertThrows(
        CommonServiceException.class,
        () -> postalCodeService.fetchCustomRegionIdByPostalCode(ORG_ID, ZIP_CODE));
    verify(postalCodePersistenceService, times(1)).fetchPostalCode(anyString(), anyString());
  }

  @Test
  void getCustomRegionIdTest1() throws PromiseEngineException, CommonServiceException {
    CustomRegionResponse customRegionResponse = testUtil.getCustomRegionResponse();
    when(postalCodePersistenceService.fetchPostalCode(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getPostalCodeEntityWithRegions()));
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCustomRegionEntity2()));
    CustomRegionResponse customRegionResponse1 =
        postalCodeService.fetchCustomRegionIdByPostalCode(ORG_ID, ZIP_CODE);
    assertEquals(customRegionResponse1.getId(), customRegionResponse.getId());
    assertEquals(
        customRegionResponse1.getCustomAttributes(), customRegionResponse.getCustomAttributes());
    verify(postalCodePersistenceService, times(1)).fetchPostalCode(anyString(), anyString());
  }

  @Test
  void getCustomRegionIdTestForEmptyPostalCode() throws PromiseEngineException {
    when(postalCodePersistenceService.fetchPostalCode(anyString(), anyString()))
        .thenReturn(Optional.empty());
    assertThrows(
        CommonServiceException.class,
        () -> postalCodeService.fetchCustomRegionIdByPostalCode(ORG_ID, ZIP_CODE));
    verify(postalCodePersistenceService, times(1)).fetchPostalCode(anyString(), anyString());
  }

  @Test
  void getCustomRegionIdTestForEmptyCustomRegions() throws PromiseEngineException {
    when(postalCodePersistenceService.fetchPostalCode(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getPostalCodeEntityWithRegions()));
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.empty());
    assertThrows(
        CommonServiceException.class,
        () -> postalCodeService.fetchCustomRegionIdByPostalCode(ORG_ID, ZIP_CODE));
    verify(postalCodePersistenceService, times(1)).fetchPostalCode(anyString(), anyString());
  }

  @Test
  void getCustomRegionIdTestForCustomRegionsRuntimeException() throws PromiseEngineException {
    when(postalCodePersistenceService.fetchPostalCode(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getPostalCodeEntityWithRegions()));
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> postalCodeService.fetchCustomRegionIdByPostalCode(ORG_ID, ZIP_CODE));
    verify(postalCodePersistenceService, times(1)).fetchPostalCode(anyString(), anyString());
  }

  @Test
  void fetchPostalCodePrefixForOrgIdAndState() throws PromiseEngineException {
    when(postalCodePersistenceService.getPostalCodePrefixForOrgIdAndState(anyString(), anyString()))
        .thenReturn(List.of(ZIP_CODE_PREFIX, POSTAL_CODE_PREFIX_2));

    List<String> postalCodeTimeZonePrefixList =
        postalCodeService.fetchPostalCodePrefixForOrgIdAndState(ORG_ID, STATE);
    Assertions.assertFalse(CollectionUtils.isEmpty(postalCodeTimeZonePrefixList));
    verify(postalCodePersistenceService, times(1))
        .getPostalCodePrefixForOrgIdAndState(anyString(), anyString());
  }

  @Test
  void fetchPostalCodeTimezoneByOrgIdAndCountryTest() throws PromiseEngineException {

    when(postalCodePersistenceService.getPostCodeTimeZoneByOrgIdAndCountry(
            anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPostalCodeEntity()));

    List<PostalCodeResponse> postalCodePrefixDtoList =
        postalCodeService.fetchPostalCodeTimezoneByOrgIdAndCountry(ORG_ID, COUNTRY);

    assertEquals(1, postalCodePrefixDtoList.size());
    assertEquals(
        testUtil.getPostalCodeEntity().getState(), postalCodePrefixDtoList.get(0).getState());
    verify(postalCodePersistenceService, times(1))
        .getPostCodeTimeZoneByOrgIdAndCountry(anyString(), anyString());
  }

  @Test
  void getMarketRegionForOrgId() throws PromiseEngineException {

    when(postalCodePersistenceService.getRecordsForOrgId(anyString()))
        .thenReturn(testUtil.getMarketRegion());

    List<MarketRegionInfo> marketRegionDtos = postalCodeService.getMarketRegionForOrgId(ORG_ID);

    assertEquals(1, marketRegionDtos.size());
    assertEquals(1, marketRegionDtos.get(0).getNoOfZipCodePrefixes());
    assertEquals(4, marketRegionDtos.get(0).getNoOfCities());

    verify(postalCodePersistenceService, times(1)).getRecordsForOrgId(anyString());
  }

  @Test
  void getMarketRegionForOrgIdException() throws PromiseEngineException {

    when(postalCodePersistenceService.getRecordsForOrgId(anyString())).thenReturn(null);

    Assertions.assertThrows(
        PromiseEngineException.class, () -> postalCodeService.getMarketRegionForOrgId(ORG_ID));

    verify(postalCodePersistenceService, times(1)).getRecordsForOrgId(anyString());
  }

  @Test
  void getMarketRegionForOrgIdExceptionOnEmptyList() throws PromiseEngineException {

    when(postalCodePersistenceService.getRecordsForOrgId(anyString())).thenReturn(List.of());

    Assertions.assertThrows(
        PromiseEngineException.class, () -> postalCodeService.getMarketRegionForOrgId(ORG_ID));

    verify(postalCodePersistenceService, times(1)).getRecordsForOrgId(anyString());
  }
}
