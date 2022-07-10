package com.hbc.postal.code.timezone.controller;

import static com.hbc.postal.code.timezone.utils.PostalCodeTimezoneConstants.ORG_ID;
import static com.hbc.postal.code.timezone.utils.PostalCodeTimezoneConstants.POSTAL_CODE_PREFIX;
import static com.hbc.postal.code.timezone.utils.PostalCodeTimezoneConstants.STATUS_CODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.common.exception.PromiseEngineException;
import com.hbc.common.response.BaseResponse;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.service.PostalCodeTimezoneService;
import com.hbc.postal.code.timezone.TestUtil;
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
  void createPostalCodeTimezoneTest() throws PromiseEngineException {
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
  void createPostalCodeTimezoneExceptionTest() throws PromiseEngineException {
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
        postalCodeTimezoneController.getPostalCodeTimezone(ORG_ID, POSTAL_CODE_PREFIX);

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
          postalCodeTimezoneController.getPostalCodeTimezone(ORG_ID, POSTAL_CODE_PREFIX);
        });
    verify(postalCodeTimezoneService, times(1)).getPostalCodeTimezone(anyString(), anyString());
  }

  @Test
  void updatePostalCodeTimezoneTest() throws PromiseEngineException {
    PostalCodeTimezoneDto postalCodeTimezoneDto = testUtil.getPostalCodeTimezoneDto();
    UpdatePostalCodeTimezoneRequest baseRequest = testUtil.getUpdatePostalCodeTimezoneRequest();
    when(postalCodeTimezoneService.updatePostalCodeTimezone(
            anyString(), anyString(), any(UpdatePostalCodeTimezoneRequest.class)))
        .thenReturn(postalCodeTimezoneDto);

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> responseEntity =
        postalCodeTimezoneController.updatePostalCodeTimezone(
            ORG_ID, POSTAL_CODE_PREFIX, baseRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(postalCodeTimezoneDto, responseEntity.getBody().getPayload());
    verify(postalCodeTimezoneService, times(1))
        .updatePostalCodeTimezone(
            anyString(), anyString(), any(UpdatePostalCodeTimezoneRequest.class));
  }

  @Test
  void updatePostalCodeTimezoneExceptionTest() throws PromiseEngineException {
    UpdatePostalCodeTimezoneRequest baseRequest = testUtil.getUpdatePostalCodeTimezoneRequest();
    when(postalCodeTimezoneService.updatePostalCodeTimezone(
            anyString(), anyString(), any(UpdatePostalCodeTimezoneRequest.class)))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeTimezoneController.updatePostalCodeTimezone(
              ORG_ID, POSTAL_CODE_PREFIX, baseRequest);
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
        postalCodeTimezoneController.deletePostalCodeTimezone(ORG_ID, POSTAL_CODE_PREFIX);

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
          postalCodeTimezoneController.deletePostalCodeTimezone(ORG_ID, POSTAL_CODE_PREFIX);
        });
    verify(postalCodeTimezoneService, times(1)).deletePostalCodeTimezone(anyString(), anyString());
  }
}
