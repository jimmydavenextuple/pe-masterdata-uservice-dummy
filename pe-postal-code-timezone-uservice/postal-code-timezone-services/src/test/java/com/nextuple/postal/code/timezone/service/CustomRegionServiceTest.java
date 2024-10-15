package com.nextuple.postal.code.timezone.service;

import static com.nextuple.postal.code.timezone.TestUtil.ID;
import static com.nextuple.postal.code.timezone.TestUtil.ORG_ID;
import static com.nextuple.postal.code.timezone.TestUtil.REGION_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.postal.code.timezone.TestUtil;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.CustomRegionRequest;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.persistence.domain.CustomRegionDomainDto;
import com.nextuple.postal.code.timezone.persistence.domain.PostalCodeDomainDto;
import com.nextuple.postal.code.timezone.persistence.service.CustomRegionPersistenceService;
import com.nextuple.postal.code.timezone.persistence.service.PostalCodePersistenceService;
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
import org.springframework.data.domain.*;

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
    createCustomRegionRequest.setCodes(Arrays.asList("X2H"));
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
  @DisplayName("FAILURE PATH:Test the create custom region request with PARTIAL codes")
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
    verify(customRegionPersistenceService, times(1))
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
    verify(customRegionPersistenceService, times(1))
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
    verify(customRegionPersistenceService, times(1))
        .fetchRegionByOrgIdAndId(anyString(), anyString());
  }

  @Test
  @DisplayName("Test the scenario when custom region already exists for given postal code")
  void createPostalCodeTestWhenCustomRegionAlreadyExistsForGivenPostalCode()
      throws PromiseEngineException, CommonServiceException {
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
  void createPostalCodeExceptionTest() throws PromiseEngineException {
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionRequest();
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCustomRegionEntity()));

    assertThrows(
        CommonServiceException.class,
        () -> {
          customRegionService.createCustomRegion(createCustomRegionRequest);
        });
    verify(customRegionPersistenceService, times(1))
        .fetchRegionByOrgIdAndId(anyString(), anyString());
  }

  @Test
  void createPostalCodeExceptionTest2() throws PromiseEngineException {
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
    verify(customRegionPersistenceService, times(1))
        .fetchRegionByOrgIdAndId(anyString(), anyString());
  }

  @Test
  void createCustomRegionTestExceptionTest() {
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionRequest();
    when(customRegionPersistenceService.fetchRegionByOrgIdAndCustomRegionName(
            anyString(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCustomRegionEntity()));

    Exception ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              customRegionService.createCustomRegion(createCustomRegionRequest);
            });

    assertEquals("Custom Region Name already exists", ex.getMessage());
    verify(customRegionPersistenceService, times(1))
        .fetchRegionByOrgIdAndId(anyString(), anyString());
  }

  @Test
  void getPostalCodeTest() throws PromiseEngineException, CommonServiceException {
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
  void getPostalCodeNotFoundTest() throws PromiseEngineException {
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
  @DisplayName(
      "FAILURE PATH:Test the update custom region request with PARTIAL codes and postal code DB with exception")
  void updatePostalCodeTest8() throws PromiseEngineException, CommonServiceException {
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionEntity();
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionRequest2();
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.of(customRegion));
    when(customRegionPersistenceService.saveCustomRegion(any(CustomRegionDomainDto.class)))
        .thenReturn(customRegion);
    when(postalCodePersistenceService.fetchPostalCodeList(anyString(), anyString()))
        .thenThrow(PromiseEngineException.class);
    assertThrows(
        PromiseEngineException.class,
        () -> customRegionService.updateCustomRegion(createCustomRegionRequest));
    verify(customRegionPersistenceService, times(1))
        .fetchRegionByOrgIdAndId(anyString(), anyString());
  }

  @Test
  @DisplayName(
      "FAILURE PATH:Test the update custom region request with PARTIAL codes and postal code DB with save exception")
  void updatePostalCodeTest9() throws PromiseEngineException {
    CustomRegionDomainDto customRegion = testUtil.getCustomRegionEntity();
    CustomRegionRequest createCustomRegionRequest = testUtil.getCreateCustomRegionRequest2();
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenReturn(Optional.of(customRegion));
    when(customRegionPersistenceService.saveCustomRegion(any(CustomRegionDomainDto.class)))
        .thenReturn(customRegion);
    when(postalCodePersistenceService.fetchPostalCodeList(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPostalCodeEntityWithRegions()));
    when(postalCodePersistenceService.savePostalCode(any()))
        .thenThrow(PromiseEngineException.class);
    assertThrows(
        PromiseEngineException.class,
        () -> customRegionService.updateCustomRegion(createCustomRegionRequest));
    verify(customRegionPersistenceService, times(1))
        .fetchRegionByOrgIdAndId(anyString(), anyString());
  }

  @Test
  void updatePostalCodeTest11() throws PromiseEngineException, CommonServiceException {
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
  void deletePostalCodeTestWithNonExistingData() {
    when(customRegionPersistenceService.fetchRegionByOrgIdAndId(anyString(), anyString()))
        .thenThrow(RuntimeException.class);
    assertThrows(RuntimeException.class, () -> customRegionService.deleteCustomRegion(ORG_ID, ID));
  }

  @Test
  void deletePostalCodeTestWithNonExistingData2()
      throws PromiseEngineException, CommonServiceException {
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
}
