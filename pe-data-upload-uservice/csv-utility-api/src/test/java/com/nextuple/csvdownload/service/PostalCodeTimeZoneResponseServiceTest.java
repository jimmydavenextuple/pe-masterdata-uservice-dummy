/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.service;

import static org.mockito.Mockito.*;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.csvdownload.exception.CustomRegionServiceException;
import com.nextuple.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionDto;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeFeign;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class PostalCodeTimeZoneResponseServiceTest {

  @Mock private PostalCodeFeign postalCodeTimezoneFeign;

  @InjectMocks private PostalCodeTimeZoneResponseService postalCodeTimeZoneResponseService;

  @InjectMocks TestUtil testUtil;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(postalCodeTimeZoneResponseService, "pageSize", 2);
  }

  @Test
  void getFsaList() throws PostalCodeTimezoneServiceException {
    when(postalCodeTimezoneFeign.getPostalCodePrefixForOrgIdAndState(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(List.of(TestUtil.DESTINATION_FSA)).build());

    List<String> fsaList =
        postalCodeTimeZoneResponseService.getFSAsByOrgIdAndState(
            TestUtil.ORG_ID, TestUtil.SOURCE_REGION);
    Assertions.assertFalse(CollectionUtils.isEmpty(fsaList));
    verify(postalCodeTimezoneFeign, times(1))
        .getPostalCodePrefixForOrgIdAndState(anyString(), anyString());
  }

  @Test
  void getFsaListEmptyList() {
    when(postalCodeTimezoneFeign.getPostalCodePrefixForOrgIdAndState(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(Collections.emptyList()).build());

    Exception exception =
        Assertions.assertThrows(
            PostalCodeTimezoneServiceException.class,
            () ->
                postalCodeTimeZoneResponseService.getFSAsByOrgIdAndState(
                    TestUtil.ORG_ID, TestUtil.SOURCE_REGION));
    Assertions.assertNotNull(exception);
    verify(postalCodeTimezoneFeign, times(1))
        .getPostalCodePrefixForOrgIdAndState(anyString(), anyString());
  }

  @Test
  void getFsaListNullResponse() {
    when(postalCodeTimezoneFeign.getPostalCodePrefixForOrgIdAndState(anyString(), anyString()))
        .thenReturn(null);

    Exception exception =
        Assertions.assertThrows(
            PostalCodeTimezoneServiceException.class,
            () ->
                postalCodeTimeZoneResponseService.getFSAsByOrgIdAndState(
                    TestUtil.ORG_ID, TestUtil.SOURCE_REGION));
    Assertions.assertNotNull(exception);
    verify(postalCodeTimezoneFeign, times(1))
        .getPostalCodePrefixForOrgIdAndState(anyString(), anyString());
  }

  @Test
  void getPostalCodeTimeZoneByOrgIdAndCountry() throws PostalCodeTimezoneServiceException {
    when(postalCodeTimezoneFeign.getPostalCodeTimeZoneForOrgIdAndCountry(anyString(), anyString()))
        .thenReturn(
            BaseResponse.builder().payload(List.of(testUtil.getPostalCodeTimezoneDto())).build());

    List<PostalCodeResponse> postalCodeResponse =
        postalCodeTimeZoneResponseService.getPostalCodeTimeZoneByOrgIdAndCountry(
            TestUtil.ORG_ID, "CANADA");
    Assertions.assertFalse(CollectionUtils.isEmpty(postalCodeResponse));
    verify(postalCodeTimezoneFeign, times(1))
        .getPostalCodeTimeZoneForOrgIdAndCountry(anyString(), anyString());
  }

  @Test
  void getPostalCodeTimeZoneByOrgIdAndCountryEmptyList() {
    when(postalCodeTimezoneFeign.getPostalCodeTimeZoneForOrgIdAndCountry(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(Collections.emptyList()).build());

    Exception exception =
        Assertions.assertThrows(
            PostalCodeTimezoneServiceException.class,
            () ->
                postalCodeTimeZoneResponseService.getPostalCodeTimeZoneByOrgIdAndCountry(
                    TestUtil.ORG_ID, "CANADA"));
    Assertions.assertNotNull(exception);
    verify(postalCodeTimezoneFeign, times(1))
        .getPostalCodeTimeZoneForOrgIdAndCountry(anyString(), anyString());
  }

  @Test
  void getPostalCodeTimeZoneByOrgIdAndCountryNullResponse() {
    when(postalCodeTimezoneFeign.getPostalCodeTimeZoneForOrgIdAndCountry(anyString(), anyString()))
        .thenReturn(null);

    Exception exception =
        Assertions.assertThrows(
            PostalCodeTimezoneServiceException.class,
            () ->
                postalCodeTimeZoneResponseService.getPostalCodeTimeZoneByOrgIdAndCountry(
                    TestUtil.ORG_ID, "CANADA"));
    Assertions.assertNotNull(exception);
    verify(postalCodeTimezoneFeign, times(1))
        .getPostalCodeTimeZoneForOrgIdAndCountry(anyString(), anyString());
  }

  @Test
  void getCustomRegionsByOrgIdTest() {
    when(postalCodeTimezoneFeign.getCustomRegionList(any(), any(), any(), any(), any()))
        .thenReturn(
            BaseResponse.builder().payload(testUtil.getCustomRegionPaginatedResponse(1)).build());

    List<CustomRegionDto> customRegionDtoList =
        postalCodeTimeZoneResponseService.getCustomRegionsByOrgId(TestUtil.ORG_ID);
    Assertions.assertFalse(CollectionUtils.isEmpty(customRegionDtoList));
    Assertions.assertEquals(4, customRegionDtoList.size());

    verify(postalCodeTimezoneFeign, times(2))
        .getCustomRegionList(any(), any(), any(), any(), any());
  }

  @Test
  void getCustomRegionsByOrgIdAndRegionIdTest() throws CustomRegionServiceException {
    BaseResponse<CustomRegionResponse> customRegionBaseResponse = new BaseResponse<>();
    customRegionBaseResponse.setSuccess(Boolean.TRUE);
    customRegionBaseResponse.setPayload(testUtil.getCustomRegionResponse());

    when(postalCodeTimezoneFeign.fetchCustomRegionDetailsByOrgIdAndId(anyString(), anyString()))
        .thenReturn(customRegionBaseResponse);

    CustomRegionResponse customRegionResponse =
        postalCodeTimeZoneResponseService.getCustomRegionsByOrgIdAndRegionId(
            TestUtil.ORG_ID, TestUtil.REGION_ID);
    Assertions.assertNotNull(customRegionResponse);

    verify(postalCodeTimezoneFeign, times(1))
        .fetchCustomRegionDetailsByOrgIdAndId(anyString(), anyString());
  }

  @Test
  void getCustomRegionsByOrgIdAndRegionIdExceptionTest() {
    BaseResponse<CustomRegionResponse> customRegionBaseResponse = new BaseResponse<>();
    customRegionBaseResponse.setSuccess(Boolean.FALSE);

    when(postalCodeTimezoneFeign.fetchCustomRegionDetailsByOrgIdAndId(anyString(), anyString()))
        .thenReturn(customRegionBaseResponse);

    Exception exception =
        Assertions.assertThrows(
            CustomRegionServiceException.class,
            () ->
                postalCodeTimeZoneResponseService.getCustomRegionsByOrgIdAndRegionId(
                    TestUtil.ORG_ID, TestUtil.REGION_ID));
    Assertions.assertNotNull(exception);
    Assertions.assertEquals(
        "Custom Region does not exist for given orgId and regionId", exception.getMessage());

    verify(postalCodeTimezoneFeign, times(1))
        .fetchCustomRegionDetailsByOrgIdAndId(anyString(), anyString());
  }
}
