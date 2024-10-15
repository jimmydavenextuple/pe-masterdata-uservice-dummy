/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.service;

import static com.nextuple.postal.code.timezone.TestUtil.*;
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
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodePrefixDto;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeTimezoneDomainDto;
import com.nextuple.postal.code.timezone.persistence.service.PostalCodeTimezonePersistenceService;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PostalCodeTimezoneServiceTest {
  @Mock private PostalCodeTimezonePersistenceService postalCodeTimezonePersistenceService;
  @InjectMocks private PostalCodeTimezoneService postalCodeTimezoneService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createPostalCodeTimezoneTest() throws PromiseEngineException, CommonServiceException {
    PostalCodeTimezoneDomainDto postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();
    CreatePostalCodeTimezoneRequest createPostalCodeTimezoneRequest =
        testUtil.getCreatePostalCodeTimezoneRequest();
    when(postalCodeTimezonePersistenceService.savePostalCodeTimezone(
            any(PostalCodeTimezoneDomainDto.class)))
        .thenReturn(postalCodeTimezoneEntity);

    PostalCodeTimezoneDto received_dto =
        postalCodeTimezoneService.createPostalCodeTimezone(createPostalCodeTimezoneRequest);
    assertEquals(createPostalCodeTimezoneRequest.getOrgId(), received_dto.getOrgId());
    verify(postalCodeTimezonePersistenceService, times(1))
        .savePostalCodeTimezone(any(PostalCodeTimezoneDomainDto.class));
  }

  @Test
  void getPostalCodeTimezoneTest() throws PromiseEngineException {
    PostalCodeTimezoneDomainDto postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();
    when(postalCodeTimezonePersistenceService.getPostalCodeTimezone(anyString(), anyString()))
        .thenReturn(postalCodeTimezoneEntity);

    PostalCodeTimezoneDto postalCodeTimezoneDto =
        postalCodeTimezoneService.getPostalCodeTimezone(ORG_ID, ZIP_CODE_PREFIX);
    assertEquals(postalCodeTimezoneDto.getOrgId(), postalCodeTimezoneEntity.getOrgId());
    verify(postalCodeTimezonePersistenceService, times(1))
        .getPostalCodeTimezone(anyString(), anyString());
  }

  @Test
  void getPostalCodeTimezoneNotFoundTest() throws PromiseEngineException {
    when(postalCodeTimezonePersistenceService.getPostalCodeTimezone(anyString(), anyString()))
        .thenReturn(null);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeTimezoneService.getPostalCodeTimezone(ORG_ID, ZIP_CODE_PREFIX);
        });
    verify(postalCodeTimezonePersistenceService, times(1))
        .getPostalCodeTimezone(anyString(), anyString());
  }

  @Test
  void updatePostalCodeTimezoneTest() throws PromiseEngineException, CommonServiceException {
    PostalCodeTimezoneDomainDto postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();
    UpdatePostalCodeTimezoneRequest updatePostalCodeTimezoneRequest =
        testUtil.getUpdatePostalCodeTimezoneRequest();

    when(postalCodeTimezonePersistenceService.getPostalCodeTimezone(anyString(), anyString()))
        .thenReturn(postalCodeTimezoneEntity);
    when(postalCodeTimezonePersistenceService.savePostalCodeTimezone(
            any(PostalCodeTimezoneDomainDto.class)))
        .thenReturn(postalCodeTimezoneEntity);

    PostalCodeTimezoneDto updated_postalCodeTimezoneDto =
        postalCodeTimezoneService.updatePostalCodeTimezone(
            ORG_ID, ZIP_CODE_PREFIX, updatePostalCodeTimezoneRequest);
    assertEquals(
        updatePostalCodeTimezoneRequest.getCountry(), updated_postalCodeTimezoneDto.getCountry());
    assertEquals(
        updatePostalCodeTimezoneRequest.getCity(), updated_postalCodeTimezoneDto.getCity());
    verify(postalCodeTimezonePersistenceService, times(1))
        .getPostalCodeTimezone(anyString(), anyString());
    verify(postalCodeTimezonePersistenceService, times(1))
        .savePostalCodeTimezone(any(PostalCodeTimezoneDomainDto.class));
  }

  @Test
  void deletePostalCodeTimezoneTest() throws PromiseEngineException {
    PostalCodeTimezoneDomainDto postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();

    when(postalCodeTimezonePersistenceService.getPostalCodeTimezone(anyString(), anyString()))
        .thenReturn(postalCodeTimezoneEntity);
    when(postalCodeTimezonePersistenceService.deletePostalCodeTimezone(
            any(PostalCodeTimezoneDomainDto.class)))
        .thenReturn(postalCodeTimezoneEntity);

    PostalCodeTimezoneDto deleted_postalCodeTimezoneDto =
        postalCodeTimezoneService.deletePostalCodeTimezone(ORG_ID, ZIP_CODE_PREFIX);
    assertEquals(postalCodeTimezoneEntity.getOrgId(), deleted_postalCodeTimezoneDto.getOrgId());
  }

  @Test
  void fetchPostalCodePrefixListTest() throws PromiseEngineException {
    PostalCodeTimezoneDomainDto postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();

    when(postalCodeTimezonePersistenceService.getPostalCodeTimezoneForOrgId(anyString()))
        .thenReturn(List.of(postalCodeTimezoneEntity));

    List<PostalCodePrefixDto> postalCodePrefixDtoList =
        postalCodeTimezoneService.fetchPostalCodePrefixList(ORG_ID);

    assertEquals(1, postalCodePrefixDtoList.size());
    assertEquals(postalCodeTimezoneEntity.getState(), postalCodePrefixDtoList.get(0).getState());
    verify(postalCodeTimezonePersistenceService, times(1))
        .getPostalCodeTimezoneForOrgId(anyString());
  }

  @Test
  void createPostalCodeTimezoneTestException() throws PromiseEngineException {
    CreatePostalCodeTimezoneRequest request1 = testUtil.getCreatePostalCodeTimezoneRequest();
    request1.setCountry("IND");
    CreatePostalCodeTimezoneRequest request2 = testUtil.getCreatePostalCodeTimezoneRequest();
    request2.setTimeZone("IST");

    Assertions.assertThrows(
        CommonServiceException.class,
        () -> postalCodeTimezoneService.createPostalCodeTimezone(request1));
    Assertions.assertThrows(
        CommonServiceException.class,
        () -> postalCodeTimezoneService.createPostalCodeTimezone(request2));
    verify(postalCodeTimezonePersistenceService, times(0))
        .savePostalCodeTimezone(any(PostalCodeTimezoneDomainDto.class));
  }
}
