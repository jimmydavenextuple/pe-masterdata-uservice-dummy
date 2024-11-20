/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nextuple.postal.code.timezone.TestUtil;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeTimezoneDomainDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class PostalCodeTimezoneMapperTest {

  @InjectMocks private TestUtil testUtil;

  private static final PostalCodeTimezoneMapper INSTANCE =
      Mappers.getMapper(PostalCodeTimezoneMapper.class);

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void convertToPostalCodeTimezoneEntityTest() {
    PostalCodeTimezoneDto postalCodeTimezoneDto = testUtil.getPostalCodeTimezoneDto();
    PostalCodeTimezoneDomainDto postalCodeTimezoneEntity =
        INSTANCE.convertToPostalCodeTimezoneEntity(postalCodeTimezoneDto);
    assertEquals(postalCodeTimezoneDto.getOrgId(), postalCodeTimezoneEntity.getOrgId());
  }

  @Test
  void convertToPostalCodeTimezoneDtoTest() {
    PostalCodeTimezoneDomainDto postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();
    PostalCodeTimezoneDto postalCodeTimezoneDto =
        INSTANCE.convertToPostalCodeTimezoneDto(postalCodeTimezoneEntity);
    assertEquals(postalCodeTimezoneEntity.getOrgId(), postalCodeTimezoneDto.getOrgId());
  }

  @Test
  void convertFromCreatePostalCodeTimezoneRequestToEntityTest() {
    CreatePostalCodeTimezoneRequest createPostalCodeTimezoneRequest =
        testUtil.getCreatePostalCodeTimezoneRequest();
    PostalCodeTimezoneDomainDto postalCodeTimezoneEntity =
        INSTANCE.convertFromCreatePostalCodeTimezoneRequestToEntity(
            createPostalCodeTimezoneRequest);
    assertEquals(createPostalCodeTimezoneRequest.getOrgId(), postalCodeTimezoneEntity.getOrgId());
  }

  @Test
  void insertValuesFromUpdatePostalCodeTimezoneRequestToEntityTest() {
    UpdatePostalCodeTimezoneRequest updatePostalCodeTimezoneRequest =
        testUtil.getUpdatePostalCodeTimezoneRequest();
    PostalCodeTimezoneDomainDto postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();

    INSTANCE.insertValuesFromUpdatePostalCodeTimezoneRequestToEntity(
        updatePostalCodeTimezoneRequest, postalCodeTimezoneEntity);
    assertEquals(updatePostalCodeTimezoneRequest.getCity(), postalCodeTimezoneEntity.getCity());
  }
}
