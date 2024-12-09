package com.nextuple.postal.code.timezone.controller;

import static com.nextuple.postal.code.timezone.TestUtil.COUNTRY;
import static com.nextuple.postal.code.timezone.TestUtil.ID;
import static com.nextuple.postal.code.timezone.TestUtil.ORG_ID;
import static com.nextuple.postal.code.timezone.TestUtil.STATUS_CODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.postal.code.timezone.TestUtil;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionDto;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.inbound.CustomRegionRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.DeleteCustomRegionGeozonesRequest;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.service.CustomRegionService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CustomRegionControllerTest {
  @Mock private CustomRegionService customRegionService;
  @InjectMocks private CustomRegionController customRegionController;
  @InjectMocks private TestUtil testUtil;
  @Mock private PageProperties pageProperties;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createCustomRegionRequestTest() throws PromiseEngineException, CommonServiceException {
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionRequest();
    CustomRegionResponse customRegionResponse = testUtil.getCustomRegionResponse();
    when(customRegionService.createCustomRegion(any(CustomRegionRequest.class)))
        .thenReturn(customRegionResponse);

    ResponseEntity<BaseResponse<CustomRegionResponse>> responseEntity =
        customRegionController.createCustomRegion(createCustomRegionRequest);

    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(customRegionResponse, responseEntity.getBody().getPayload());

    verify(customRegionService, times(1)).createCustomRegion(any(CustomRegionRequest.class));
  }

  @Test
  void createCustomRegionRequestExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionRequest();
    when(customRegionService.createCustomRegion(any(CustomRegionRequest.class)))
        .thenThrow(PromiseEngineException.class);
    assertThrows(
        PromiseEngineException.class,
        () -> {
          customRegionController.createCustomRegion(createCustomRegionRequest);
        });

    verify(customRegionService, times(1)).createCustomRegion(any(CustomRegionRequest.class));
  }

  @Test
  void getCustomRegionResponseTest() throws CommonServiceException {
    CustomRegionResponse customRegionResponse = testUtil.getCustomRegionResponse();
    when(customRegionService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(customRegionResponse);

    ResponseEntity<BaseResponse<CustomRegionResponse>> responseEntity =
        customRegionController.fetchCustomRegionDetailsByOrgIdAndId(ORG_ID, ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(customRegionResponse, responseEntity.getBody().getPayload());
    verify(customRegionService, times(1)).fetchRegionByOrgIdAndId(anyString(), anyString());
  }

  @Test
  void getCustomRegionResponseExceptionTest() throws CommonServiceException {
    when(customRegionService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenThrow(RuntimeException.class);

    assertThrows(
        RuntimeException.class,
        () -> {
          customRegionController.fetchCustomRegionDetailsByOrgIdAndId(ORG_ID, ID);
        });
    verify(customRegionService, times(1)).fetchRegionByOrgIdAndId(anyString(), anyString());
  }

  @Test
  void updateCustomRegionTest() throws PromiseEngineException, CommonServiceException {
    CustomRegionResponse customRegionResponse = testUtil.getCustomRegionResponse();
    CustomRegionRequest baseRequest = testUtil.getCreateCustomRegionRequest();
    when(customRegionService.updateCustomRegion(any(CustomRegionRequest.class)))
        .thenReturn(customRegionResponse);

    ResponseEntity<BaseResponse<CustomRegionResponse>> responseEntity =
        customRegionController.updateCustomRegion(baseRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(customRegionResponse, responseEntity.getBody().getPayload());
    verify(customRegionService, times(1)).updateCustomRegion(any(CustomRegionRequest.class));
  }

  @Test
  void updateCustomRegionExceptionTest() throws PromiseEngineException, CommonServiceException {
    CustomRegionRequest baseRequest = testUtil.getCreateCustomRegionFullRequest();
    when(customRegionService.updateCustomRegion(any(CustomRegionRequest.class)))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          customRegionController.updateCustomRegion(baseRequest);
        });
    verify(customRegionService, times(1)).updateCustomRegion(any(CustomRegionRequest.class));
  }

  @Test
  void deletePostalCodeTest() throws PromiseEngineException, CommonServiceException {
    CustomRegionResponse customRegionResponse = testUtil.getCustomRegionResponse();
    when(customRegionService.deleteCustomRegion(anyString(), anyString()))
        .thenReturn(customRegionResponse);

    ResponseEntity<BaseResponse<CustomRegionResponse>> responseEntity =
        customRegionController.deleteCustomRegion(ORG_ID, ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(customRegionResponse, responseEntity.getBody().getPayload());
    verify(customRegionService, times(1)).deleteCustomRegion(anyString(), anyString());
  }

  @Test
  @DisplayName("Happy Path : Delete zip_code_prefixes from given custom region")
  void deleteCustomRegionZipCodePrefixesTest()
      throws PromiseEngineException, CommonServiceException {
    CustomRegionResponse customRegionResponse = testUtil.getCustomRegionResponse();
    DeleteCustomRegionGeozonesRequest request =
        DeleteCustomRegionGeozonesRequest.builder().codes(List.of("T2P")).id(ID).build();
    when(customRegionService.deleteCustomRegionGeozones(anyString(), anyString(), any()))
        .thenReturn(customRegionResponse);

    ResponseEntity<BaseResponse<CustomRegionResponse>> responseEntity =
        customRegionController.deleteCustomRegionGeoZones(ORG_ID, request);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(customRegionResponse, responseEntity.getBody().getPayload());
    verify(customRegionService, times(1))
        .deleteCustomRegionGeozones(anyString(), anyString(), any());
  }

  @Test
  void deletePostalCodeExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(customRegionService.deleteCustomRegion(anyString(), anyString()))
        .thenThrow(PromiseEngineException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          customRegionController.deleteCustomRegion(ORG_ID, ID);
        });
    verify(customRegionService, times(1)).deleteCustomRegion(anyString(), anyString());
  }

  @Test
  void getPaginatedCustomRegionListTest() throws CommonServiceException, PromiseEngineException {
    List<CustomRegionDto> customRegionDtoList = testUtil.getCustomRegionDtoList();

    when(customRegionService.getCustomRegionListByOrgId(any(), any(), any(), any(), any()))
        .thenReturn(
            testUtil.getCustomRegionDtoPage(2, customRegionDtoList, customRegionDtoList.size()));

    ResponseEntity<BaseResponse<PagePayload<CustomRegionDto>>> response =
        customRegionController.getCustomRegionList(
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.of(2),
                Optional.of(1),
                Optional.of(TestUtil.SORT_BY),
                Optional.of(TestUtil.SORT_ORDER_DESC)));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getTotalPages(),
        "Pagination Total pages");
    Assertions.assertEquals(
        customRegionDtoList.size(),
        (int) response.getBody().getPayload().getPagination().getTotalRecords(),
        "Total Elements");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getCurrentPage(),
        "Current page number");
    Assertions.assertEquals(
        customRegionDtoList.size(),
        response.getBody().getPayload().getData().size(),
        "Paginated data");
    Assertions.assertEquals(
        "", response.getBody().getPayload().getPagination().getNext(), "Next Uri should be empty");
    Assertions.assertEquals(
        Boolean.TRUE,
        Objects.nonNull(response.getBody().getPayload().getPagination().getPrevious()),
        "Previous Uri should not be null");

    verify(customRegionService, times(1))
        .getCustomRegionListByOrgId(any(), any(), any(), any(), any());
  }

  @Test
  void getPaginatedCustomRegionListDefaultTest()
      throws CommonServiceException, PromiseEngineException {
    List<CustomRegionDto> customRegionDtoList = testUtil.getCustomRegionDtoList();

    when(pageProperties.getPageNo()).thenReturn(1);
    when(pageProperties.getPageSize()).thenReturn(15);
    when(customRegionService.getCustomRegionListByOrgId(any(), any(), any(), any(), any()))
        .thenReturn(
            testUtil.getCustomRegionDtoPage(2, customRegionDtoList, customRegionDtoList.size()));

    ResponseEntity<BaseResponse<PagePayload<CustomRegionDto>>> response =
        customRegionController.getCustomRegionList(
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getTotalPages(),
        "Pagination Total pages");
    Assertions.assertEquals(
        customRegionDtoList.size(),
        (int) response.getBody().getPayload().getPagination().getTotalRecords(),
        "Total Elements");
    Assertions.assertEquals(
        1,
        (int) response.getBody().getPayload().getPagination().getCurrentPage(),
        "Current page number");
    Assertions.assertEquals(
        customRegionDtoList.size(),
        response.getBody().getPayload().getData().size(),
        "Paginated data");
    Assertions.assertEquals(
        "",
        response.getBody().getPayload().getPagination().getPrevious(),
        "Previous Uri should be empty");
    Assertions.assertEquals(
        Boolean.TRUE,
        Objects.nonNull(response.getBody().getPayload().getPagination().getNext()),
        "Next Uri should not be null");

    verify(customRegionService, times(1))
        .getCustomRegionListByOrgId(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Fetch Custom region information")
  void getPaginatedCustomRegionInfoTest() throws CommonServiceException, PromiseEngineException {
    List<CustomRegionInfo> customRegionInfo = testUtil.getTwoCustomRegionInfoList();
    Pageable pageable = PageRequest.of(1, 10, Sort.by(TestUtil.SORT_BY).ascending());
    Page<CustomRegionInfo> customRegionDtoPage =
        new PageImpl<>(customRegionInfo, pageable, customRegionInfo.size());

    when(customRegionService.getCustomRegionByCountryRegionIdAndName(
            any(), any(), any(), any(), any()))
        .thenReturn(customRegionDtoPage);

    ResponseEntity<BaseResponse<PagePayload<CustomRegionInfo>>> response =
        customRegionController.getCustomRegionInfo(
            ORG_ID,
            COUNTRY,
            null,
            null,
            testUtil.getPageParams(
                Optional.of(2),
                Optional.of(1),
                Optional.of(TestUtil.SORT_BY),
                Optional.of(TestUtil.SORT_ORDER_DESC)));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getTotalPages(),
        "Pagination Total pages");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getCurrentPage(),
        "Current page number");
    Assertions.assertEquals(
        customRegionInfo.size(),
        response.getBody().getPayload().getData().size(),
        "Paginated data");
    Assertions.assertEquals(
        "", response.getBody().getPayload().getPagination().getNext(), "Next Uri should be empty");
    Assertions.assertEquals(
        Boolean.TRUE,
        Objects.nonNull(response.getBody().getPayload().getPagination().getPrevious()),
        "Previous Uri should not be null");

    verify(customRegionService, times(1))
        .getCustomRegionByCountryRegionIdAndName(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Happy Path : Delete zip_code_prefixes from given custom region")
  void deleteCustomRegionByRequest() throws PromiseEngineException, CommonServiceException {
    CustomRegionResponse customRegionResponse = testUtil.getCustomRegionResponse();
    customRegionResponse.setCodes(List.of("T2P"));
    CustomRegionRequest customRegionRequest = testUtil.getCreateCustomRegionRequest4();
    customRegionRequest.setCodes(List.of("T2P"));
    when(customRegionService.deleteCustomRegionGeozones(anyString(), anyString(), any()))
        .thenReturn(customRegionResponse);
    ResponseEntity<BaseResponse<CustomRegionResponse>> responseEntity =
        customRegionController.deleteCustomRegionByRequest(customRegionRequest);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), STATUS_CODE);
    assertEquals(customRegionResponse, responseEntity.getBody().getPayload());
    verify(customRegionService, times(1))
        .deleteCustomRegionGeozones(anyString(), anyString(), any());
  }
}
