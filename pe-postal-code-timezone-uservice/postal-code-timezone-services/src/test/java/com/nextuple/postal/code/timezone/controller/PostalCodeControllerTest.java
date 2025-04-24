/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.controller;

import static com.nextuple.postal.code.timezone.TestUtil.COUNTRY;
import static com.nextuple.postal.code.timezone.TestUtil.ORG_ID;
import static com.nextuple.postal.code.timezone.TestUtil.POSTAL_CODE_PREFIX_2;
import static com.nextuple.postal.code.timezone.TestUtil.STATE;
import static com.nextuple.postal.code.timezone.TestUtil.STATUS_CODE;
import static com.nextuple.postal.code.timezone.TestUtil.ZIP_CODE;
import static com.nextuple.postal.code.timezone.TestUtil.ZIP_CODE_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.postal.code.timezone.TestUtil;
import com.nextuple.postal.code.timezone.api.domain.dto.MarketRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodePrefixDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.PostalCodeRequest;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.postal.code.timezone.domain.mapper.PostalCodeMapper;
import com.nextuple.postal.code.timezone.service.PostalCodeService;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

class PostalCodeControllerTest {

  @Mock private PostalCodeService postalCodeService;
  @InjectMocks private PostalCodeController postalCodeController;
  @InjectMocks private TestUtil testUtil;

  private static final PostalCodeMapper INSTANCE = Mappers.getMapper(PostalCodeMapper.class);

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createPostalCodeTest() throws PromiseEngineException, CommonServiceException {
    PostalCodeRequest postalCodeRequest = testUtil.getPostalCodeRequest();
    PostalCodeResponse postalCodeResponse = testUtil.getPostalCodeResponse();
    when(postalCodeService.createPostalCode(any(PostalCodeRequest.class)))
        .thenReturn(postalCodeResponse);

    ResponseEntity<BaseResponse<PostalCodeResponse>> responseEntity =
        postalCodeController.createPostalCode(postalCodeRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(postalCodeResponse, responseEntity.getBody().getPayload());

    verify(postalCodeService, times(1)).createPostalCode(any(PostalCodeRequest.class));
  }

  @Test
  void createPostalCodeExceptionTest() throws PromiseEngineException, CommonServiceException {
    PostalCodeRequest postalCodeRequest = testUtil.getPostalCodeRequest();
    when(postalCodeService.createPostalCode(any(PostalCodeRequest.class)))
        .thenThrow(PromiseEngineException.class);
    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeController.createPostalCode(postalCodeRequest);
        });

    verify(postalCodeService, times(1)).createPostalCode(any(PostalCodeRequest.class));
  }

  @Test
  void getPostalCodeTest() throws PromiseEngineException, CommonServiceException {
    PostalCodeResponse postalCodeResponsePojo = testUtil.getPostalCodeResponse();
    when(postalCodeService.fetchPostalCode(anyString(), anyString()))
        .thenReturn(postalCodeResponsePojo);

    ResponseEntity<BaseResponse<PostalCodeResponse>> responseEntity =
        postalCodeController.getPostalCode(ORG_ID, ZIP_CODE);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(postalCodeResponsePojo, responseEntity.getBody().getPayload());
    verify(postalCodeService, times(1)).fetchPostalCode(anyString(), anyString());
  }

  @Test
  void getPostalCodeExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(postalCodeService.fetchPostalCode(anyString(), anyString()))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeController.getPostalCode(ORG_ID, ZIP_CODE);
        });
    verify(postalCodeService, times(1)).fetchPostalCode(anyString(), anyString());
  }

  @Test
  void updatePostalCodeTest() throws PromiseEngineException, CommonServiceException {
    PostalCodeResponse postalCodeResponse = testUtil.getPostalCodeResponse();
    PostalCodeRequest baseRequest = testUtil.getPostalCodeRequest();
    when(postalCodeService.updatePostalCode(any(PostalCodeRequest.class)))
        .thenReturn(postalCodeResponse);

    ResponseEntity<BaseResponse<PostalCodeResponse>> responseEntity =
        postalCodeController.updatePostalCode(baseRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(postalCodeResponse, responseEntity.getBody().getPayload());
    verify(postalCodeService, times(1)).updatePostalCode(any(PostalCodeRequest.class));
  }

  @Test
  void updatePostalCodeExceptionTest() throws PromiseEngineException, CommonServiceException {
    PostalCodeRequest baseRequest = testUtil.getPostalCodeRequest();
    when(postalCodeService.updatePostalCode(any(PostalCodeRequest.class)))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeController.updatePostalCode(baseRequest);
        });
    verify(postalCodeService, times(1)).updatePostalCode(any(PostalCodeRequest.class));
  }

  @Test
  void deletePostalCodeTest() throws PromiseEngineException, CommonServiceException {
    PostalCodeResponse postalCodeResponse = testUtil.getPostalCodeResponse();
    when(postalCodeService.processRemovePostalCode(anyString(), anyString()))
        .thenReturn(postalCodeResponse);

    ResponseEntity<BaseResponse<PostalCodeResponse>> responseEntity =
        postalCodeController.deletePostalCode(ORG_ID, ZIP_CODE);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(postalCodeResponse, responseEntity.getBody().getPayload());
    verify(postalCodeService, times(1)).processRemovePostalCode(anyString(), anyString());
  }

  @Test
  void deletePostalCodeExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(postalCodeService.processRemovePostalCode(anyString(), anyString()))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeController.deletePostalCode(ORG_ID, ZIP_CODE);
        });
    verify(postalCodeService, times(1)).processRemovePostalCode(anyString(), anyString());
  }

  @Test
  void getPostalCodePrefixTest() throws PromiseEngineException, CommonServiceException {
    PostalCodeResponse postalCodeResponse = testUtil.getPostalCodeResponse();

    when(postalCodeService.fetchByPostalCodePrefix(anyString(), anyString()))
        .thenReturn(List.of(postalCodeResponse));

    ResponseEntity<BaseResponse<List<PostalCodeResponse>>> responseEntity =
        postalCodeController.getByPostalCodePrefix(ORG_ID, ZIP_CODE_PREFIX);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(List.of(postalCodeResponse), responseEntity.getBody().getPayload());
    verify(postalCodeService, times(1)).fetchByPostalCodePrefix(anyString(), anyString());
  }

  @Test
  void getPostalCodePrefixExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(postalCodeService.fetchByPostalCodePrefix(anyString(), anyString()))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeController.getByPostalCodePrefix(ORG_ID, ZIP_CODE_PREFIX);
        });
    verify(postalCodeService, times(1)).fetchByPostalCodePrefix(anyString(), anyString());
  }

  @Test
  void getCustomRegionIdTest() throws PromiseEngineException, CommonServiceException {
    CustomRegionResponse customRegionResponse = testUtil.getCustomRegionResponse();
    when(postalCodeService.fetchCustomRegionIdByPostalCode(anyString(), anyString()))
        .thenReturn(customRegionResponse);

    ResponseEntity<BaseResponse<CustomRegionResponse>> responseEntity =
        postalCodeController.fetchCustomRegionIdByPostalCode(ORG_ID, ZIP_CODE);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(customRegionResponse, responseEntity.getBody().getPayload());
    verify(postalCodeService, times(1)).fetchCustomRegionIdByPostalCode(anyString(), anyString());
  }

  @Test
  void getCustomRegionIdTestExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(postalCodeService.fetchCustomRegionIdByPostalCode(anyString(), anyString()))
        .thenThrow(PromiseEngineException.class);
    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeController.fetchCustomRegionIdByPostalCode(ORG_ID, ZIP_CODE);
        });
    verify(postalCodeService, times(1)).fetchCustomRegionIdByPostalCode(anyString(), anyString());
  }

  @Test
  void getPostalCodePrefixForOrgIdAndState() throws PromiseEngineException {
    when(postalCodeService.fetchPostalCodePrefixForOrgIdAndState(anyString(), anyString()))
        .thenReturn(List.of(ZIP_CODE_PREFIX, POSTAL_CODE_PREFIX_2));

    ResponseEntity<BaseResponse<List<String>>> responseEntity =
        postalCodeController.getPostalCodePrefixForOrgIdAndState(ORG_ID, STATE);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    Assertions.assertFalse(CollectionUtils.isEmpty(responseEntity.getBody().getPayload()));
    verify(postalCodeService, times(1))
        .fetchPostalCodePrefixForOrgIdAndState(anyString(), anyString());
  }

  @Test
  void getPostalCodeTimeZoneForOrgIdAndCountry() throws PromiseEngineException {
    when(postalCodeService.fetchPostalCodeTimezoneByOrgIdAndCountry(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPostalCodeResponse()));

    ResponseEntity<BaseResponse<List<PostalCodeResponse>>> responseEntity =
        postalCodeController.getPostalCodeTimeZoneForOrgIdAndCountry(ORG_ID, COUNTRY);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    Assertions.assertFalse(CollectionUtils.isEmpty(responseEntity.getBody().getPayload()));
    verify(postalCodeService, times(1))
        .fetchPostalCodeTimezoneByOrgIdAndCountry(anyString(), anyString());
  }

  @Test
  void getMarketRegionsForOrgId() throws PromiseEngineException {
    when(postalCodeService.getMarketRegionForOrgId(anyString()))
        .thenReturn(INSTANCE.convertToMarketRegionInfo(testUtil.getMarketRegion()));

    ResponseEntity<BaseResponse<List<MarketRegionInfo>>> responseEntity =
        postalCodeController.getMarketRegionsForOrgId(ORG_ID);
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    verify(postalCodeService, times(1)).getMarketRegionForOrgId(anyString());
  }

  @Test
  void getPostalCodePrefixListTest() throws PromiseEngineException {
    PostalCodePrefixDto postalCodePrefixDto = testUtil.getPostalCodePrefixDto();
    when(postalCodeService.getPostalCodePrefixList(anyString()))
        .thenReturn(List.of(postalCodePrefixDto));

    ResponseEntity<BaseResponse<List<PostalCodePrefixDto>>> responseEntity =
        postalCodeController.fetchPostalCodePrefixList(ORG_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(postalCodePrefixDto, responseEntity.getBody().getPayload().get(0));
    verify(postalCodeService, times(1)).getPostalCodePrefixList(anyString());
  }

  @Test
  void getPostalCodePrefixListExceptionTest() throws PromiseEngineException {
    when(postalCodeService.getPostalCodePrefixList(anyString()))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeController.fetchPostalCodePrefixList(ORG_ID);
        });

    verify(postalCodeService, times(1)).getPostalCodePrefixList(anyString());
  }

  @Test
  @DisplayName("Should successfully upsert zip code and return OK status with payload")
  void upsertPostalCodeSuccessTest() throws PromiseEngineException, CommonServiceException {
    PostalCodeRequest postalCodeRequest = testUtil.getPostalCodeRequest();
    PostalCodeResponse postalCodeResponse = testUtil.getPostalCodeResponse();

    when(postalCodeService.upsertPostalCode(any(PostalCodeRequest.class)))
        .thenReturn(postalCodeResponse);

    ResponseEntity<BaseResponse<PostalCodeResponse>> responseEntity =
        postalCodeController.upsertPostalCode(postalCodeRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    assertEquals(postalCodeResponse, responseEntity.getBody().getPayload());

    verify(postalCodeService, times(1)).upsertPostalCode(any(PostalCodeRequest.class));
  }

  @Test
  @DisplayName("Should throw PromiseEngineException when service fails during upsert")
  void upsertPostalCodeExceptionTest() throws PromiseEngineException, CommonServiceException {
    PostalCodeRequest postalCodeRequest = testUtil.getPostalCodeRequest();

    when(postalCodeService.upsertPostalCode(any(PostalCodeRequest.class)))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> postalCodeController.upsertPostalCode(postalCodeRequest));

    verify(postalCodeService, times(1)).upsertPostalCode(any(PostalCodeRequest.class));
  }
}
