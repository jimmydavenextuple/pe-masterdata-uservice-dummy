/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.controller;

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
import com.nextuple.common.response.BaseResponse;
import com.nextuple.postal.code.timezone.TestUtil;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodePrefixDto;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.service.PostalCodeTimezoneService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class PostalCodeTimezoneControllerTest {

  @Mock private PostalCodeTimezoneService postalCodeTimezoneService;
  @InjectMocks private PostalCodeTimezoneController postalCodeTimezoneController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createPostalCodeTimezoneTest() throws PromiseEngineException, CommonServiceException {
    CreatePostalCodeTimezoneRequest createPostalCodeTimezoneRequest =
        testUtil.getCreatePostalCodeTimezoneRequest();
    PostalCodeTimezoneDto postalCodeTimezoneDto = testUtil.getPostalCodeTimezoneDto();
    when(postalCodeTimezoneService.createPostalCodeTimezone(
            any(CreatePostalCodeTimezoneRequest.class)))
        .thenReturn(postalCodeTimezoneDto);

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> responseEntity =
        postalCodeTimezoneController.createPostalCodeTimezone(createPostalCodeTimezoneRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(postalCodeTimezoneDto, responseEntity.getBody().getPayload());

    verify(postalCodeTimezoneService, times(1))
        .createPostalCodeTimezone(any(CreatePostalCodeTimezoneRequest.class));
  }

  @Test
  void createPostalCodeTimezoneExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    CreatePostalCodeTimezoneRequest createPostalCodeTimezoneRequest =
        testUtil.getCreatePostalCodeTimezoneRequest();
    when(postalCodeTimezoneService.createPostalCodeTimezone(
            any(CreatePostalCodeTimezoneRequest.class)))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeTimezoneService.createPostalCodeTimezone(createPostalCodeTimezoneRequest);
        });

    verify(postalCodeTimezoneService, times(1))
        .createPostalCodeTimezone(any(CreatePostalCodeTimezoneRequest.class));
  }

  @Test
  void getPostalCodeTimezoneTest() throws PromiseEngineException {
    PostalCodeTimezoneDto postalCodeTimezoneDto = testUtil.getPostalCodeTimezoneDto();
    when(postalCodeTimezoneService.getPostalCodeTimezone(anyString(), anyString()))
        .thenReturn(postalCodeTimezoneDto);

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> responseEntity =
        postalCodeTimezoneController.getPostalCodeTimezone(ORG_ID, ZIP_CODE_PREFIX);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(postalCodeTimezoneDto, responseEntity.getBody().getPayload());
    verify(postalCodeTimezoneService, times(1)).getPostalCodeTimezone(anyString(), anyString());
  }

  @Test
  void getPostalCodeTimezoneExceptionTest() throws PromiseEngineException {
    when(postalCodeTimezoneService.getPostalCodeTimezone(anyString(), anyString()))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeTimezoneController.getPostalCodeTimezone(ORG_ID, ZIP_CODE_PREFIX);
        });
    verify(postalCodeTimezoneService, times(1)).getPostalCodeTimezone(anyString(), anyString());
  }

  @Test
  void updatePostalCodeTimezoneTest() throws PromiseEngineException, CommonServiceException {
    PostalCodeTimezoneDto postalCodeTimezoneDto = testUtil.getPostalCodeTimezoneDto();
    UpdatePostalCodeTimezoneRequest baseRequest = testUtil.getUpdatePostalCodeTimezoneRequest();
    when(postalCodeTimezoneService.updatePostalCodeTimezone(
            anyString(), anyString(), any(UpdatePostalCodeTimezoneRequest.class)))
        .thenReturn(postalCodeTimezoneDto);

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> responseEntity =
        postalCodeTimezoneController.updatePostalCodeTimezone(ORG_ID, ZIP_CODE_PREFIX, baseRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(postalCodeTimezoneDto, responseEntity.getBody().getPayload());
    verify(postalCodeTimezoneService, times(1))
        .updatePostalCodeTimezone(
            anyString(), anyString(), any(UpdatePostalCodeTimezoneRequest.class));
  }

  @Test
  void updatePostalCodeTimezoneExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    UpdatePostalCodeTimezoneRequest baseRequest = testUtil.getUpdatePostalCodeTimezoneRequest();
    when(postalCodeTimezoneService.updatePostalCodeTimezone(
            anyString(), anyString(), any(UpdatePostalCodeTimezoneRequest.class)))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeTimezoneController.updatePostalCodeTimezone(
              ORG_ID, ZIP_CODE_PREFIX, baseRequest);
        });
    verify(postalCodeTimezoneService, times(1))
        .updatePostalCodeTimezone(
            anyString(), anyString(), any(UpdatePostalCodeTimezoneRequest.class));
  }

  @Test
  void deletePostalCodeTimezoneTest() throws PromiseEngineException {
    PostalCodeTimezoneDto postalCodeTimezoneDto = testUtil.getPostalCodeTimezoneDto();
    when(postalCodeTimezoneService.deletePostalCodeTimezone(anyString(), anyString()))
        .thenReturn(postalCodeTimezoneDto);

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> responseEntity =
        postalCodeTimezoneController.deletePostalCodeTimezone(ORG_ID, ZIP_CODE_PREFIX);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(postalCodeTimezoneDto, responseEntity.getBody().getPayload());
    verify(postalCodeTimezoneService, times(1)).deletePostalCodeTimezone(anyString(), anyString());
  }

  @Test
  void deletePostalCodeTimezoneExceptionTest() throws PromiseEngineException {
    when(postalCodeTimezoneService.deletePostalCodeTimezone(anyString(), anyString()))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeTimezoneController.deletePostalCodeTimezone(ORG_ID, ZIP_CODE_PREFIX);
        });
    verify(postalCodeTimezoneService, times(1)).deletePostalCodeTimezone(anyString(), anyString());
  }

  @Test
  void getPostalCodePrefixListTest() throws PromiseEngineException {
    PostalCodePrefixDto postalCodePrefixDto = testUtil.getPostalCodePrefixDto();
    when(postalCodeTimezoneService.fetchPostalCodePrefixList(anyString()))
        .thenReturn(List.of(postalCodePrefixDto));

    ResponseEntity<BaseResponse<List<PostalCodePrefixDto>>> responseEntity =
        postalCodeTimezoneController.getPostalCodePrefixList(ORG_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(postalCodePrefixDto, responseEntity.getBody().getPayload().get(0));
    verify(postalCodeTimezoneService, times(1)).fetchPostalCodePrefixList(anyString());
  }

  @Test
  void getPostalCodePrefixListExceptionTest() throws PromiseEngineException {
    when(postalCodeTimezoneService.fetchPostalCodePrefixList(anyString()))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeTimezoneController.getPostalCodePrefixList(ORG_ID);
        });

    verify(postalCodeTimezoneService, times(1)).fetchPostalCodePrefixList(anyString());
  }
}
