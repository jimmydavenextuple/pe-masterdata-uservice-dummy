package com.nextuple.postal.code.timezone.service;

import static com.nextuple.postal.code.timezone.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.postal.code.timezone.TestUtil;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionDto;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.inbound.CustomRegionRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.DeleteCustomRegionGeozonesRequest;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.persistence.domain.CustomRegionDomainDto;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeDomainDto;
import com.nextuple.postal.code.timezone.persistence.service.CustomRegionPersistenceService;
import com.nextuple.postal.code.timezone.persistence.service.PostalCodePersistenceService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class CustomRegionServiceTest {
  @Mock private CustomRegionPersistenceService customRegionPersistenceService;
  @InjectMocks private CustomRegionService customRegionService;
  @Mock private PostalCodePersistenceService postalCodePersistenceService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("HAPPY PATH:Test the create custom region request with PARTIAL codes")
  void createCustomRegionTest() throws PromiseEngineException, CommonServiceException {
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionEntity();
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionRequest();
    createCustomRegionRequest.setCodes(List.of("X2H"));
    when(customRegionPersistenceService.saveCustomRegion(any(CustomRegionDomainDto.class)))
        .thenReturn(customRegion);
    when(postalCodePersistenceService.fetchPostalCodeList(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPostalCodeEntity()));
    CustomRegionResponse customRegionResponse =
        customRegionService.createCustomRegion(createCustomRegionRequest);
    assertEquals(customRegion.getOrgId(), customRegionResponse.getOrgId());
    verify(customRegionPersistenceService, times(1))
        .saveCustomRegion(any(CustomRegionDomainDto.class));
  }

  @Test
  @DisplayName("HAPPY PATH:Test the upsert existing custom region")
  void createCustomRegionTestUpsert() throws PromiseEngineException, CommonServiceException {
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionEntity();
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionRequest();
    createCustomRegionRequest.setCodes(List.of("X2H"));
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(any(), any()))
        .thenReturn(Optional.ofNullable(customRegion));
    when(customRegionPersistenceService.saveCustomRegion(any(CustomRegionDomainDto.class)))
        .thenReturn(customRegion);
    when(postalCodePersistenceService.fetchPostalCodeList(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPostalCodeEntity()));
    CustomRegionResponse customRegionResponse =
        customRegionService.createCustomRegion(createCustomRegionRequest);
    assertEquals(customRegion.getOrgId(), customRegionResponse.getOrgId());
    verify(customRegionPersistenceService, times(1))
        .saveCustomRegion(any(CustomRegionDomainDto.class));
  }

  @Test
  @DisplayName("FAILURE PATH:Test the create custom region request with INVALID codes")
  void createPostalCodeTest3() throws PromiseEngineException {
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionEntity();
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionFullRequest();
    when(customRegionPersistenceService.saveCustomRegion(any(CustomRegionDomainDto.class)))
        .thenReturn(customRegion);
    when(postalCodePersistenceService.fetchPostalCode(anyString(), anyString()))
        .thenReturn(Optional.empty());
    assertThrows(
        CommonServiceException.class,
        () -> {
          customRegionService.createCustomRegion(createCustomRegionRequest);
        });
    verify(customRegionPersistenceService, times(0))
        .fetchRegionByOrgIdAndId(anyString(), anyString());
  }

  @Test
  @DisplayName(
      "FAILURE PATH:Test the create custom region request with PARTIAL codes and Domain class failing")
  void createPostalCodeTest5() throws PromiseEngineException {
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionEntity();
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionRequest();
    when(customRegionPersistenceService.saveCustomRegion(any(CustomRegionDomainDto.class)))
        .thenReturn(customRegion);
    when(postalCodePersistenceService.fetchPostalCodeList(anyString(), anyString()))
        .thenThrow(PromiseEngineException.class);
    assertThrows(
        PromiseEngineException.class,
        () -> {
          customRegionService.createCustomRegion(createCustomRegionRequest);
        });
    verify(customRegionPersistenceService, times(0))
        .fetchRegionByOrgIdAndId(anyString(), anyString());
  }

  @Test
  @DisplayName(
      "FAILURE PATH:Test the create custom region request with PARTIAL codes and identifier already existing")
  void createPostalCodeTest7() throws PromiseEngineException {
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionEntity();
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionRequest();
    when(customRegionPersistenceService.saveCustomRegion(any(CustomRegionDomainDto.class)))
        .thenReturn(customRegion);
    PostalCodeDomainDto postalCodeEntity = testUtil.getPostalCodeEntity();
    postalCodeEntity.setCustomRegion(REGION_ID);
    when(postalCodePersistenceService.fetchPostalCodeList(anyString(), anyString()))
        .thenReturn(List.of(postalCodeEntity));
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(ORG_ID, ID))
        .thenReturn(Optional.of(testUtil.getCustomRegionEntity()));
    assertThrows(
        CommonServiceException.class,
        () -> {
          customRegionService.createCustomRegion(createCustomRegionRequest);
        });
    verify(customRegionPersistenceService, times(0))
        .fetchRegionByOrgIdAndId(anyString(), anyString());
  }

  @Test
  @DisplayName("Test the scenario when custom region already exists for given postal code")
  void createPostalCodeTestWhenCustomRegionAlreadyExistsForGivenPostalCode()
      throws PromiseEngineException {
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionEntity();
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionFullRequest();
    createCustomRegionRequest.setCodes(Arrays.asList("X2H"));
    when(customRegionPersistenceService.saveCustomRegion(any(CustomRegionDomainDto.class)))
        .thenReturn(customRegion);
    PostalCodeDomainDto postalCodeEntity = testUtil.getPostalCodeEntity();
    postalCodeEntity.setCustomRegion("CRID1");
    when(postalCodePersistenceService.fetchPostalCodeList(anyString(), anyString()))
        .thenReturn(List.of(postalCodeEntity));
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              customRegionService.createCustomRegion(createCustomRegionRequest);
            });
    assertEquals("Custom Region already exists for the given code", ex.getMessage());
    verify(customRegionPersistenceService, times(0))
        .saveCustomRegion(any(CustomRegionDomainDto.class));
  }

  @Test
  @DisplayName("Test the scenario when custom region already exists for given zip code")
  void createCustomRegionExceptionTestWhenCustomRegionAlreadyExistsForGivenZipCode()
      throws PromiseEngineException {
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionEntity();
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionFullRequest();
    createCustomRegionRequest.setCodes(Arrays.asList("T2P", "X2H"));
    when(customRegionPersistenceService.saveCustomRegion(any(CustomRegionDomainDto.class)))
        .thenReturn(customRegion);
    PostalCodeDomainDto postalCodeEntity = testUtil.getPostalCodeEntity();
    postalCodeEntity.setCustomRegion("CRID1");

    PostalCodeDomainDto postalCodeEntity1 = testUtil.getPostalCodeEntity();
    postalCodeEntity1.setZipCode("T2P01");
    postalCodeEntity1.setZipCodePrefix("T2P");

    when(postalCodePersistenceService.fetchPostalCodeList(anyString(), anyString()))
        .thenReturn(List.of(postalCodeEntity, postalCodeEntity1));
    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              customRegionService.createCustomRegion(createCustomRegionRequest);
            });
    assertEquals("Custom Region already exists for the given code", ex.getMessage());
    verify(customRegionPersistenceService, times(0))
        .saveCustomRegion(any(CustomRegionDomainDto.class));
    verify(postalCodePersistenceService, times(0)).savePostalCode(any(PostalCodeDomainDto.class));
  }

  @Test
  void createPostalCodeExceptionTest() {
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionRequest();
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCustomRegionEntity()));

    assertThrows(
        CommonServiceException.class,
        () -> {
          customRegionService.createCustomRegion(createCustomRegionRequest);
        });
    verify(customRegionPersistenceService, times(0))
        .fetchRegionByOrgIdAndId(anyString(), anyString());
  }

  @Test
  void createPostalCodeExceptionTest2() {
    CustomRegionRequest createCustomRegionRequest =
        testUtil.getCreateCustomRegionEmptyCodesRequest();
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.empty());

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              customRegionService.createCustomRegion(createCustomRegionRequest);
            });
    assertEquals("Zip Codes cannot be blank", ex.getMessage());
    verify(customRegionPersistenceService, times(0))
        .fetchRegionByOrgIdAndId(anyString(), anyString());
  }

  @Test
  @DisplayName("Create custom region with zip prefixes from multiple countries.")
  void createCustomRegionExceptionMultipleCountryZipPrefix() throws PromiseEngineException {
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionRequest();
    when(postalCodePersistenceService.fetchPostalCodeList(
            createCustomRegionRequest.getOrgId(), "T2P"))
        .thenReturn(testUtil.getPostalCodeEntityListCountry("CA"));
    when(postalCodePersistenceService.fetchPostalCodeList(
            createCustomRegionRequest.getOrgId(), "T3P"))
        .thenReturn(testUtil.getPostalCodeEntityListCountry("US"));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              customRegionService.createCustomRegion(createCustomRegionRequest);
            });
    assertEquals("Zip code prefix associated with multiple countries", ex.getMessage());
    verify(postalCodePersistenceService, times(2)).fetchPostalCodeList(any(), any());
    verify(customRegionPersistenceService, times(0)).saveCustomRegion(any());
  }

  @Test
  void getPostalCodeTest() throws CommonServiceException {
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionEntity();
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.ofNullable(customRegion));

    CustomRegionResponse customRegionResponse =
        customRegionService.fetchRegionByOrgIdAndId(ORG_ID, ID);
    assertEquals(customRegionResponse.getOrgId(), customRegion.getOrgId());
    verify(customRegionPersistenceService, times(1))
        .fetchRegionByOrgIdAndId(anyString(), anyString());
  }

  @Test
  void getPostalCodeNotFoundTest() {
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.empty());

    assertThrows(
        CommonServiceException.class,
        () -> {
          customRegionService.fetchRegionByOrgIdAndId(ORG_ID, ID);
        });
    verify(customRegionPersistenceService, times(1))
        .fetchRegionByOrgIdAndId(anyString(), anyString());
  }

  @Test
  void getPostalCodeNotFoundTest2() {
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenThrow(RuntimeException.class);

    assertThrows(
        RuntimeException.class, () -> customRegionService.fetchRegionByOrgIdAndId(ORG_ID, ID));
    verify(customRegionPersistenceService, times(1))
        .fetchRegionByOrgIdAndId(anyString(), anyString());
  }

  @Test
  @DisplayName("HAPPY PATH:Test the update custom region request with PARTIAL codes")
  void updatePostalCodeTest1() throws PromiseEngineException, CommonServiceException {
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionEntity();
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionRequest();
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.of(customRegion));
    when(customRegionPersistenceService.saveCustomRegion(any(CustomRegionDomainDto.class)))
        .thenReturn(customRegion);
    when(postalCodePersistenceService.fetchPostalCodeList(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPostalCodeEntity()));
    CustomRegionResponse customRegionResponse =
        customRegionService.updateCustomRegion(createCustomRegionRequest);
    assertEquals(customRegion.getOrgId(), customRegionResponse.getOrgId());
    verify(customRegionPersistenceService, times(1))
        .saveCustomRegion(any(CustomRegionDomainDto.class));
  }

  @Test
  void updatePostalCodeTest2() throws PromiseEngineException, CommonServiceException {
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionEntity();
    customRegion.setCodes(Arrays.asList("T2P"));
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionRequest();
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.of(customRegion));
    when(customRegionPersistenceService.saveCustomRegion(any(CustomRegionDomainDto.class)))
        .thenReturn(customRegion);
    PostalCodeDomainDto postalCodeEntity1 = testUtil.getPostalCodeEntity();
    postalCodeEntity1.setZipCodePrefix("T2P");
    PostalCodeDomainDto postalCodeEntity2 = testUtil.getPostalCodeEntity();
    postalCodeEntity2.setZipCodePrefix("T3P");
    when(postalCodePersistenceService.fetchPostalCodeList(anyString(), anyString()))
        .thenReturn(List.of(postalCodeEntity1, postalCodeEntity2));
    CustomRegionResponse customRegionResponse =
        customRegionService.updateCustomRegion(createCustomRegionRequest);
    assertEquals(customRegion.getOrgId(), customRegionResponse.getOrgId());
    verify(customRegionPersistenceService, times(1))
        .saveCustomRegion(any(CustomRegionDomainDto.class));
  }

  @Test
  @DisplayName(
      "HAPPY PATH:Test the update custom region request with PARTIAL codes and remove data where Postal Code region data is not empty")
  void updatePostalCodeTest7() throws PromiseEngineException, CommonServiceException {
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionEntity();
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionRequest2();
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.of(customRegion));
    when(customRegionPersistenceService.saveCustomRegion(any(CustomRegionDomainDto.class)))
        .thenReturn(customRegion);
    when(postalCodePersistenceService.fetchPostalCodeList(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPostalCodeEntity()));
    CustomRegionResponse customRegionResponse =
        customRegionService.updateCustomRegion(createCustomRegionRequest);
    assertEquals(customRegion.getOrgId(), customRegionResponse.getOrgId());
    verify(customRegionPersistenceService, times(1))
        .saveCustomRegion(any(CustomRegionDomainDto.class));
  }

  @Test
  @DisplayName("Happy Path : Updating existing list of zip_code_prefixes for valid custom region")
  void updatePostalCodeTest8() throws PromiseEngineException, CommonServiceException {
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionEntity();
    CustomRegionDomainDto updatedCustomRegion = testUtil.getCustomRegionEntity();
    CustomRegionResponse expectedResponse = testUtil.getCustomRegionResponseCrId2();
    updatedCustomRegion.setCodes(expectedResponse.getCodes());
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionRequest();
    createCustomRegionRequest.setCodes(List.of("X2H"));
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.of(customRegion));
    when(customRegionPersistenceService.saveCustomRegion(any(CustomRegionDomainDto.class)))
        .thenReturn(updatedCustomRegion);
    when(postalCodePersistenceService.fetchPostalCodeList(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPostalCodeEntity()));
    CustomRegionResponse customRegionResponse =
        customRegionService.updateCustomRegion(createCustomRegionRequest);
    assertEquals(expectedResponse, customRegionResponse);
    verify(customRegionPersistenceService, times(1))
        .fetchRegionByOrgIdAndId(anyString(), anyString());
  }

  @Test
  void updatePostalCodeTest11() throws PromiseEngineException {
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionFullEntity();
    CustomRegionRequest createCustomRegionRequest =
        testUtil.getCreateCustomRegionEmptyCodesRequest();
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.of(customRegion));
    when(customRegionPersistenceService.saveCustomRegion(any(CustomRegionDomainDto.class)))
        .thenReturn(customRegion);
    when(postalCodePersistenceService.fetchPostalCode(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getPostalCodeEntity()));
    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> customRegionService.updateCustomRegion(createCustomRegionRequest));

    assertEquals("Zip Codes cannot be blank", ex.getMessage());
    verify(customRegionPersistenceService, times(0))
        .saveCustomRegion(any(CustomRegionDomainDto.class));
  }

  @Test
  void updatePostalCodeTestWithNonExistingData() {
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionFullRequest3();
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> customRegionService.updateCustomRegion(createCustomRegionRequest));
  }

  @Test
  void deletePostalCodeTest() throws PromiseEngineException, CommonServiceException {
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionEntity();
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.of(customRegion));
    doNothing()
        .when(customRegionPersistenceService)
        .deleteCustomRegion(any(CustomRegionDomainDto.class));
    CustomRegionResponse customRegionResponse = customRegionService.deleteCustomRegion(ORG_ID, ID);
    assertEquals(customRegion.getOrgId(), customRegionResponse.getOrgId());
    verify(customRegionPersistenceService, times(1))
        .deleteCustomRegion(any(CustomRegionDomainDto.class));
  }

  @Test
  @DisplayName("Happy Path : Delete valid zip_code_prefix from a valid custom region")
  void deleteCustomRegionTestV2() throws PromiseEngineException, CommonServiceException {
    DeleteCustomRegionGeozonesRequest deleteRequest =
        DeleteCustomRegionGeozonesRequest.builder().codes(List.of("X2H")).build();
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionEntity();
    List<String> codes = new ArrayList<>();
    codes.add("X2H");
    codes.add("T2P");
    customRegion.setCodes(codes);
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.of(customRegion));
    when(postalCodePersistenceService.fetchPostalCodeList(any(), any()))
        .thenReturn(List.of(testUtil.getPostalCodeEntity()));
    CustomRegionResponse customRegionResponse =
        customRegionService.deleteCustomRegionGeozones(ORG_ID, ID, deleteRequest);
    assertEquals(customRegion.getOrgId(), customRegionResponse.getOrgId());
    verify(customRegionPersistenceService, times(0))
        .deleteCustomRegion(any(CustomRegionDomainDto.class));
  }

  @Test
  @DisplayName("Happy Path : Delete valid zip_code_prefix such that custom region will be deleted")
  void deleteCustomRegionTestV2CompleteCustomRegion()
      throws PromiseEngineException, CommonServiceException {
    DeleteCustomRegionGeozonesRequest deleteRequest =
        testUtil.getDeleteCompleteCustomRegionRequest();
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionEntity();
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.of(customRegion));
    doNothing()
        .when(customRegionPersistenceService)
        .deleteCustomRegion(any(CustomRegionDomainDto.class));
    when(postalCodePersistenceService.fetchPostalCodeList(any(), any()))
        .thenReturn(testUtil.getPostalCodeEntityList());
    CustomRegionResponse customRegionResponse =
        customRegionService.deleteCustomRegionGeozones(ORG_ID, ID, deleteRequest);
    assertEquals(customRegion.getOrgId(), customRegionResponse.getOrgId());
    verify(customRegionPersistenceService, times(1))
        .deleteCustomRegion(any(CustomRegionDomainDto.class));
  }

  @Test
  @DisplayName(
      "Happy Path : Delete valid zip_code_prefix such that zip_code_prefix provided does not exist")
  void deleteCustomRegionTestV2InvalidZipCodePrefixes() throws PromiseEngineException {
    DeleteCustomRegionGeozonesRequest deleteRequest =
        DeleteCustomRegionGeozonesRequest.builder().codes(List.of("S1P", "S2P")).build();
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionEntity();
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.of(customRegion));
    doNothing()
        .when(customRegionPersistenceService)
        .deleteCustomRegion(any(CustomRegionDomainDto.class));
    when(postalCodePersistenceService.fetchPostalCodeList(any(), any()))
        .thenReturn(testUtil.getPostalCodeEntityList1());
    String message = "Zip code prefixes are not part of custom region";
    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> customRegionService.deleteCustomRegionGeozones(ORG_ID, ID, deleteRequest));
    assertEquals(message, ex.getMessage());
    verify(customRegionPersistenceService, times(0))
        .deleteCustomRegion(any(CustomRegionDomainDto.class));
  }

  @Test
  void deletePostalCodeTestWithNonExistingData() {
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenThrow(RuntimeException.class);
    assertThrows(RuntimeException.class, () -> customRegionService.deleteCustomRegion(ORG_ID, ID));
  }

  @Test
  void deletePostalCodeTestWithNonExistingData2() {
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.empty());
    assertThrows(
        CommonServiceException.class,
        () -> customRegionService.deleteCustomRegion(anyString(), anyString()));
  }

  @Test
  @DisplayName("SUCCESS PATH : Get custom regions for given orgId and page properties")
  void getCustomRegionListByOrgIdAndPagePropertiesTest()
      throws PromiseEngineException, CommonServiceException {
    List<CustomRegionDomainDto> customRegionDomainDtos = testUtil.getCustomRegionEntityList();

    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).ascending());
    Page<CustomRegionDomainDto> customRegionDomainDtoPage =
        new PageImpl<>(customRegionDomainDtos, pageable, customRegionDomainDtos.size());

    when(customRegionPersistenceService.getCustomRegionListByOrgId(
            any(), any(), any(), any(), any()))
        .thenReturn(customRegionDomainDtoPage);

    Page<CustomRegionDto> response =
        customRegionService.getCustomRegionListByOrgId(
            TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, TestUtil.SORT_ORDER_DESC);

    Assertions.assertEquals(customRegionDomainDtos.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());
    Assertions.assertEquals("id: ASC", response.getSort().toString());

    verify(customRegionPersistenceService, Mockito.times(1))
        .getCustomRegionListByOrgId(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("SUCCESS PATH : Get custom regions for given orgId and default page properties")
  void getCustomRegionListByOrgIdDefaultSortOrderTest()
      throws PromiseEngineException, CommonServiceException {
    List<CustomRegionDomainDto> customRegionDomainDtos = testUtil.getCustomRegionEntityList();

    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).ascending());
    Page<CustomRegionDomainDto> customRegionDtoPage =
        new PageImpl<>(customRegionDomainDtos, pageable, customRegionDomainDtos.size());

    when(customRegionPersistenceService.getCustomRegionListByOrgId(
            any(), any(), any(), any(), any()))
        .thenReturn(customRegionDtoPage);

    Page<CustomRegionDto> response =
        customRegionService.getCustomRegionListByOrgId(
            TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, TestUtil.DEFAULT_SORT_ORDER);

    Assertions.assertEquals(customRegionDomainDtos.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());
    Assertions.assertEquals("id: ASC", response.getSort().toString());

    verify(customRegionPersistenceService, Mockito.times(1))
        .getCustomRegionListByOrgId(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("FAILURE PATH : When invalid sort order is passed")
  void getCustomRegionListExceptionTestWhenInvalidSortOrderPassed() throws PromiseEngineException {
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                customRegionService.getCustomRegionListByOrgId(
                    TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, "invalid sort order"));

    Assertions.assertEquals(
        "Invalid sort order, consider giving either ASC or DESC", exception.getMessage());
    verify(customRegionPersistenceService, Mockito.times(0))
        .getCustomRegionListByOrgId(any(), any(), any(), any(), any());
  }

  @Test
  @DisplayName("Happy Path : Fetch Custom regions based on country and orgId only")
  void fetchCustomRegionsByOrgIdAndCountry() throws CommonServiceException, PromiseEngineException {
    PageParams pageParams =
        new PageParams(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("ASC"));
    when(postalCodePersistenceService.getCustomRegionInfoByOrgIdAndCountry(any(), any(), any()))
        .thenReturn(testUtil.getCustomRegionProjectionPage());
    when(customRegionPersistenceService.fetchCustomRegionsByIdsAndOrgId(any(), any()))
        .thenReturn(testUtil.getCustomRegionEntityList2());
    Page<CustomRegionInfo> customRegionInfoPage =
        customRegionService.getCustomRegionByCountryRegionIdAndName(
            ORG_ID, COUNTRY, null, null, pageParams);
    assertEquals(ID, customRegionInfoPage.getContent().getFirst().getCustomRegionId());
    verify(customRegionPersistenceService, times(1)).fetchCustomRegionsByIdsAndOrgId(any(), any());
    verify(postalCodePersistenceService, times(1))
        .getCustomRegionInfoByOrgIdAndCountry(any(), any(), any());
  }

  @Test
  @DisplayName(
      "Happy Path : Fetch Custom regions based on country and orgId and custom region ID(s)")
  void fetchCustomRegionsByOrgIdAndCountryAndRegionIds()
      throws CommonServiceException, PromiseEngineException {
    PageParams pageParams =
        new PageParams(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("ASC"));
    when(postalCodePersistenceService.fetchCustomRegionInfoByOrgIdAndRegionId(any(), any(), any()))
        .thenReturn(testUtil.getCustomRegionProjectionPage());
    when(customRegionPersistenceService.fetchCustomRegionsByIdsAndOrgId(any(), any()))
        .thenReturn(testUtil.getCustomRegionEntityList2());
    Page<CustomRegionInfo> customRegionInfoPage =
        customRegionService.getCustomRegionByCountryRegionIdAndName(
            ORG_ID, COUNTRY, "CRID1,CRID2", null, pageParams);
    assertEquals(ID, customRegionInfoPage.getContent().getFirst().getCustomRegionId());
    verify(customRegionPersistenceService, times(1)).fetchCustomRegionsByIdsAndOrgId(any(), any());
    verify(postalCodePersistenceService, times(1))
        .fetchCustomRegionInfoByOrgIdAndRegionId(any(), any(), any());
  }

  @Test
  @DisplayName(
      "Happy Path : Fetch Custom regions based on country and orgId and custom region name")
  void fetchCustomRegionsByOrgIdAndCountryAndRegionNames()
      throws CommonServiceException, PromiseEngineException {
    PageParams pageParams =
        new PageParams(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("ASC"));
    when(postalCodePersistenceService.fetchCustomRegionInfoByOrgIdAndRegionId(any(), any(), any()))
        .thenReturn(testUtil.getCustomRegionProjectionPage());
    when(customRegionPersistenceService.fetchCustomRegionByNamesAndOrgId(any(), any()))
        .thenReturn(testUtil.getCustomRegionEntityList2());
    when(customRegionPersistenceService.fetchCustomRegionsByIdsAndOrgId(any(), any()))
        .thenReturn(testUtil.getCustomRegionEntityList2());
    Page<CustomRegionInfo> customRegionInfoPage =
        customRegionService.getCustomRegionByCountryRegionIdAndName(
            ORG_ID, COUNTRY, null, CUSTOM_REGION_NAME, pageParams);
    assertEquals(ID, customRegionInfoPage.getContent().getFirst().getCustomRegionId());
    verify(customRegionPersistenceService, times(1)).fetchCustomRegionByNamesAndOrgId(any(), any());
    verify(customRegionPersistenceService, times(1)).fetchCustomRegionsByIdsAndOrgId(any(), any());
    verify(postalCodePersistenceService, times(1))
        .fetchCustomRegionInfoByOrgIdAndRegionId(any(), any(), any());
  }

  @Test
  @DisplayName(
      "Happy Path : Fetch Custom regions based on country and orgId and custom region name and region Ids")
  void fetchCustomRegionsByOrgIdAndCountryAndRegionNamesAndIds()
      throws CommonServiceException, PromiseEngineException {
    PageParams pageParams =
        new PageParams(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("ASC"));
    when(postalCodePersistenceService.fetchCustomRegionInfoByOrgIdAndRegionId(any(), any(), any()))
        .thenReturn(testUtil.getCustomRegionProjectionPage());
    when(customRegionPersistenceService.fetchCustomRegionsByCustomRegionIdsAndNamesAndOrgId(
            any(), any(), any()))
        .thenReturn(Optional.ofNullable(testUtil.getCustomRegionEntityList2()));
    when(customRegionPersistenceService.fetchCustomRegionsByIdsAndOrgId(any(), any()))
        .thenReturn(testUtil.getCustomRegionEntityList2());
    Page<CustomRegionInfo> customRegionInfoPage =
        customRegionService.getCustomRegionByCountryRegionIdAndName(
            ORG_ID, COUNTRY, "CRID1,CRID2", CUSTOM_REGION_NAME, pageParams);
    assertEquals(ID, customRegionInfoPage.getContent().getFirst().getCustomRegionId());
    verify(customRegionPersistenceService, times(1))
        .fetchCustomRegionsByCustomRegionIdsAndNamesAndOrgId(any(), any(), any());
    verify(customRegionPersistenceService, times(1)).fetchCustomRegionsByIdsAndOrgId(any(), any());
    verify(postalCodePersistenceService, times(1))
        .fetchCustomRegionInfoByOrgIdAndRegionId(any(), any(), any());
  }

  @Test
  @DisplayName("Search results not found for the filtered custom region name and id")
  void emptyPageContentRegionNameAndIdNotFound()
      throws CommonServiceException, PromiseEngineException {
    PageParams pageParams =
        new PageParams(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("ASC"));
    when(postalCodePersistenceService.fetchCustomRegionInfoByOrgIdAndRegionId(any(), any(), any()))
        .thenReturn(Page.empty());
    when(customRegionPersistenceService.fetchCustomRegionsByCustomRegionIdsAndNamesAndOrgId(
            any(), any(), any()))
        .thenReturn(Optional.empty());
    when(customRegionPersistenceService.fetchCustomRegionsByIdsAndOrgId(any(), any()))
        .thenReturn(testUtil.getCustomRegionEntityList2());
    Page<CustomRegionInfo> customRegionInfoPage =
        customRegionService.getCustomRegionByCountryRegionIdAndName(
            ORG_ID, COUNTRY, "CRID1,CRID2", CUSTOM_REGION_NAME, pageParams);
    assertEquals(Page.empty(), customRegionInfoPage);
    verify(customRegionPersistenceService, times(1))
        .fetchCustomRegionsByCustomRegionIdsAndNamesAndOrgId(any(), any(), any());
    verify(customRegionPersistenceService, times(1)).fetchCustomRegionsByIdsAndOrgId(any(), any());
    verify(postalCodePersistenceService, times(0))
        .fetchCustomRegionInfoByOrgIdAndRegionId(any(), any(), any());
  }

  @Test
  @DisplayName("Exception when an invalid sort order is detected")
  void invalidSortOrderException() {
    String orgId = "NEXTUPLE_GR";
    String country = "US";
    PageParams pageParams = new PageParams();
    pageParams.setSortOrder(Optional.of("INVALID_SORT_ORDER"));
    Assertions.assertThrows(
        CommonServiceException.class,
        () -> {
          customRegionService.getCustomRegionByCountryRegionIdAndName(
              orgId, country, null, null, pageParams);
        });
  }
}
