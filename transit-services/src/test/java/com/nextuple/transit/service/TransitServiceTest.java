/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.DateValidationUtil;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeFeign;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.transit.TestUtil;
import com.nextuple.transit.domain.dto.TransitTimeEntriesDto;
import com.nextuple.transit.domain.inbound.DistinctGeozonesResponse;
import com.nextuple.transit.domain.inbound.TransitBufferCreationRequest;
import com.nextuple.transit.domain.inbound.TransitDataCreationRequest;
import com.nextuple.transit.domain.inbound.TransitDataUpdationRequest;
import com.nextuple.transit.domain.outbound.TransitResponse;
import com.nextuple.transit.persistence.domain.TransitBufferDomainDto;
import com.nextuple.transit.persistence.domain.TransitDomainDto;
import com.nextuple.transit.persistence.exception.TransitDomainException;
import com.nextuple.transit.persistence.service.impl.TransitBufferPersistenceServiceImpl;
import com.nextuple.transit.persistence.service.impl.TransitPersistenceServiceImpl;
import feign.FeignException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;

class TransitServiceTest {

  @InjectMocks private TransitService transitService;

  @InjectMocks private TestUtil testUtil;

  @Mock private TransitPersistenceServiceImpl transitPersistenceService;

  @Mock private TransitBufferPersistenceServiceImpl transitBufferPersistenceService;

  @Mock private DateValidationUtil dateValidationUtil;

  @Mock private CarrierFeign carrierFeign;

  @Mock private PostalCodeFeign postalCodeFeign;
  private final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    threadPoolTaskExecutor.initialize();
    ReflectionTestUtils.setField(transitService, "threadPoolTaskExecutor", threadPoolTaskExecutor);
  }

  @Test
  void addTransitDetailsTest() throws TransitDomainException, CommonServiceException {
    TransitDataCreationRequest transitDataCreationRequest =
        testUtil.getTransitDataCreationRequest(TestUtil.TRANSIT_DAYS);
    when(postalCodeFeign.getByPostalCodePrefix(any(), any()))
        .thenReturn(testUtil.getPostalCodeResponse());
    when(transitPersistenceService.saveTransitDomainDto(any(TransitDomainDto.class)))
        .thenReturn(testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS));
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getCarrierServiceUpdateResponse());
    TransitBufferDomainDto transitBufferEntity =
        testUtil.getTransitBufferDomainDto(testUtil.ORG_ID);
    when(transitBufferPersistenceService
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.of(transitBufferEntity));
    TransitResponse transitResponse =
        transitService.addTransitInfo(
            testUtil.getTransitDataCreationRequest(TestUtil.TRANSIT_DAYS));
    assertEquals(
        testUtil.getTransitResponse(TestUtil.TRANSIT_DAYS).getCarrierServiceId(),
        transitResponse.getCarrierServiceId());
    assertEquals(transitDataCreationRequest.getBufferDays(), transitResponse.getBufferDays());

    when(transitBufferPersistenceService
            .findByOrgIdAndCarrierServiceIdAndSourceGeozoneAndDestinationGeozone(
                any(), any(), any(), any()))
        .thenReturn(Optional.empty());
    transitResponse =
        transitService.addTransitInfo(
            testUtil.getTransitDataCreationRequest(TestUtil.TRANSIT_DAYS));
    assertEquals(
        testUtil.getTransitResponse(TestUtil.TRANSIT_DAYS).getCarrierServiceId(),
        transitResponse.getCarrierServiceId());
    assertEquals(transitDataCreationRequest.getBufferDays(), transitResponse.getBufferDays());

    verify(transitPersistenceService, times(2)).saveTransitDomainDto(any(TransitDomainDto.class));
  }

  @Test
  void addTransitDetailsTestException() {
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(null);
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            transitService.addTransitInfo(
                testUtil.getTransitDataCreationRequest(TestUtil.TRANSIT_DAYS)));
  }

  @Test
  void addTransitDetailsTestException2() {
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenThrow(FeignException.class);
    Assertions.assertThrows(
        CommonServiceException.class,
        () ->
            transitService.addTransitInfo(
                testUtil.getTransitDataCreationRequest(TestUtil.TRANSIT_DAYS)));
  }

  @Test
  void addTransitDetailsForInvalidGeoZoneTest2() throws TransitDomainException {
    BaseResponse<List<PostalCodeResponse>> response = new BaseResponse<>();
    response.setPayload(null);
    response.setSuccess(true);

    when(postalCodeFeign.getByPostalCodePrefix(any(), any())).thenReturn(response);
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getCarrierServiceUpdateResponse());
    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitService.addTransitInfo(
                    testUtil.getTransitDataCreationRequest(TestUtil.TRANSIT_DAYS)));
    assertEquals("geoZone is not valid", ex.getMessage());
    verify(transitPersistenceService, times(0)).save(any(TransitDomainDto.class));
  }

  @Test
  void addTransitDetailsForInvalidCarrierTest() throws TransitDomainException {
    BaseResponse<List<CarrierServiceResponse>> response = new BaseResponse<>();
    response.setPayload(null);

    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(response);
    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitService.addTransitInfo(
                    testUtil.getTransitDataCreationRequest(TestUtil.TRANSIT_DAYS)));
    assertEquals(
        "Transit data cannot be created with given carrierServiceId and orgId", ex.getMessage());
    verify(transitPersistenceService, times(0)).saveTransitDomainDto(any(TransitDomainDto.class));
  }

  @Test
  void addTransitDetailsForInvalidCarrierEmptyTest() throws TransitDomainException {
    BaseResponse<List<CarrierServiceResponse>> response = new BaseResponse<>();
    response.setPayload(new ArrayList<>());

    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(response);
    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitService.addTransitInfo(
                    testUtil.getTransitDataCreationRequest(TestUtil.TRANSIT_DAYS)));
    assertEquals(
        "Transit data cannot be created with given carrierServiceId and orgId", ex.getMessage());
    verify(transitPersistenceService, times(0)).save(any(TransitDomainDto.class));
  }

  @Test
  void addTransitDetailsNullBufferDaysTest() throws TransitDomainException, CommonServiceException {
    TransitDataCreationRequest transitDataCreationRequest =
        testUtil.getTransitDataCreationRequest(TestUtil.TRANSIT_DAYS);
    when(postalCodeFeign.getByPostalCodePrefix(any(), any()))
        .thenReturn(testUtil.getPostalCodeResponse());
    transitDataCreationRequest.setBufferDays(null);
    TransitDomainDto transitDomainDto = testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS);
    transitDomainDto.setBufferDays(null);

    when(transitPersistenceService.saveTransitDomainDto(any(TransitDomainDto.class)))
        .thenReturn(transitDomainDto);
    when(carrierFeign.getCarrierServiceDetailsByCarrierServiceIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getCarrierServiceUpdateResponse());
    TransitResponse transitResponse = transitService.addTransitInfo(transitDataCreationRequest);
    Assertions.assertEquals(
        testUtil.getTransitResponse(TestUtil.TRANSIT_DAYS).getCarrierServiceId(),
        transitResponse.getCarrierServiceId());
    Assertions.assertNull(transitResponse.getBufferDays());
    verify(transitPersistenceService, times(1)).saveTransitDomainDto(any(TransitDomainDto.class));
  }

  @Test
  void addTransitDetailsForInvalidStartAndEndDate()
      throws TransitDomainException, CommonServiceException {
    TransitDataCreationRequest transitDataCreationRequest =
        testUtil.getTransitDataCreationRequestWithMentionedDates(
            TestUtil.TRANSIT_DAYS, new Date(8000), new Date(1000));
    doThrow(new CommonServiceException(HttpStatus.BAD_REQUEST, null, null))
        .when(dateValidationUtil)
        .validateBufferStartAndEndDate(any(), any());
    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transitService.addTransitInfo(transitDataCreationRequest));
    assertNotNull(ex);
    verify(transitPersistenceService, times(0)).saveTransitDomainDto(any(TransitDomainDto.class));
  }

  @Test
  void updateTransitBufferDetailsTest() throws TransitDomainException, CommonServiceException {
    TransitDomainDto transitDomainDto = testUtil.getTransitDomainDto3(TestUtil.BUFFER_DAYS);
    TransitBufferCreationRequest transitBufferCreationRequest =
        testUtil.getTransitBufferCreationRequest(5.0);
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(Optional.of(transitDomainDto));

    when(transitPersistenceService.saveTransitDomainDto(any()))
        .thenReturn(testUtil.getTransitDomainDto3(5.0));

    TransitResponse transitResponse =
        transitService.updateTransitBufferDetails(transitBufferCreationRequest);
    Assertions.assertEquals(testUtil.getTransitResponse2(5.0), transitResponse);

    verify(transitPersistenceService, times(1)).saveTransitDomainDto(any(TransitDomainDto.class));
    verify(transitPersistenceService, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  void updateTransitBufferDetailsTestException()
      throws TransitDomainException, CommonServiceException {

    TransitBufferCreationRequest transitBufferCreationRequest = new TransitBufferCreationRequest();
    transitBufferCreationRequest.setBufferDays(5.0);
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transitService.updateTransitBufferDetails(transitBufferCreationRequest));
    Assertions.assertEquals("Transit data not found with given details", exception.getMessage());

    verify(transitPersistenceService, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  void updateTransitBufferDetailsNegativeTransitSumTestException()
      throws TransitDomainException, CommonServiceException {
    TransitDomainDto transitDomainDto = testUtil.getTransitDomainDto3(TestUtil.BUFFER_DAYS);
    TransitBufferCreationRequest transitBufferCreationRequest =
        testUtil.getTransitBufferCreationRequest(-15.0);
    doNothing().when(dateValidationUtil).validateBufferStartAndEndDate(any(), any());
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn((Optional.of(transitDomainDto)));

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transitService.updateTransitBufferDetails(transitBufferCreationRequest));
    Assertions.assertEquals(
        "The sum of transit and buffer days is less or equal to 0", exception.getMessage());

    verify(transitPersistenceService, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  void updateTransitDetailsTest() throws TransitDomainException, CommonServiceException {
    TransitDomainDto transitEntity = testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS);
    TransitDataUpdationRequest transitDataUpdationRequest =
        testUtil.getTransitDataUpdationRequest(13.5F);
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(Optional.of(transitEntity));

    when(transitPersistenceService.saveTransitDomainDto(any()))
        .thenReturn(testUtil.getTransitDomainDto(13.5F));

    TransitResponse transitResponse =
        transitService.updateTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID,
            transitDataUpdationRequest);
    Assertions.assertEquals(testUtil.getTransitResponse(13.5F), transitResponse);

    verify(transitPersistenceService, times(1)).saveTransitDomainDto(any(TransitDomainDto.class));
    verify(transitPersistenceService, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  void updateTransitDetailsTestException() throws TransitDomainException {

    TransitDataUpdationRequest transitDataUpdationRequest = new TransitDataUpdationRequest();
    transitDataUpdationRequest.setTransitDays(13.5F);
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitService.updateTransitDetails(
                    TestUtil.ORG_ID,
                    TestUtil.SOURCE_GEOZONE,
                    TestUtil.DESTINATION_GEOZONE,
                    TestUtil.CARRIER_SERVICE_ID,
                    transitDataUpdationRequest));
    Assertions.assertEquals("Transit data not found with given details", exception.getMessage());

    verify(transitPersistenceService, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  void getTransitDetailsTest1() throws TransitDomainException, CommonServiceException {
    TransitDomainDto transitEntity = testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS);
    when(transitPersistenceService.filterAndGetTransitDetails(any(), any(), any(), any(), any()))
        .thenReturn(List.of(transitEntity));

    TransitResponse transitResponse =
        transitService.getTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION);
    Assertions.assertEquals(testUtil.getTransitResponse(TestUtil.TRANSIT_DAYS), transitResponse);
    verify(transitPersistenceService, times(1))
        .filterAndGetTransitDetails(any(), any(), any(), any(), any());
  }

  @Test
  void getTransitDetailsTest2() throws TransitDomainException, CommonServiceException {
    List<TransitDomainDto> transitDomainDtos = new ArrayList<>();
    transitDomainDtos.add(testUtil.getTransitDomainDtos("ALL"));
    transitDomainDtos.add(testUtil.getTransitDomainDtos("ALL-" + TestUtil.SERVICE_OPTION));
    when(transitPersistenceService.filterAndGetTransitDetails(any(), any(), any(), any(), any()))
        .thenReturn(transitDomainDtos);

    TransitResponse transitResponse =
        transitService.getTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            "ALL",
            TestUtil.SERVICE_OPTION);
    Assertions.assertEquals(
        "ALL-" + TestUtil.SERVICE_OPTION, transitResponse.getCarrierServiceId());
    verify(transitPersistenceService, times(1))
        .filterAndGetTransitDetails(any(), any(), any(), any(), any());
  }

  @Test
  void getTransitDetailsTest3() throws TransitDomainException, CommonServiceException {
    List<TransitDomainDto> transitDomainDtos = new ArrayList<>();
    transitDomainDtos.add(testUtil.getTransitDomainDtos("ALL"));
    transitDomainDtos.add(testUtil.getTransitDomainDtos("ALL-" + TestUtil.SERVICE_OPTION));
    transitDomainDtos.add(testUtil.getTransitDomainDtos("PURO-EXPRESS"));
    when(transitPersistenceService.filterAndGetTransitDetails(any(), any(), any(), any(), any()))
        .thenReturn(transitDomainDtos);

    TransitResponse transitResponse =
        transitService.getTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            "PURO-EXPRESS",
            TestUtil.SERVICE_OPTION);
    Assertions.assertEquals("PURO-EXPRESS", transitResponse.getCarrierServiceId());
    verify(transitPersistenceService, times(1))
        .filterAndGetTransitDetails(any(), any(), any(), any(), any());
  }

  @Test
  void getTransitDetailsTestException() throws TransitDomainException {
    List<TransitDomainDto> transitDomainDtoList = Collections.emptyList();
    when(transitPersistenceService.filterAndGetTransitDetails(any(), any(), any(), any(), any()))
        .thenReturn(transitDomainDtoList);

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitService.getTransitDetails(
                    TestUtil.ORG_ID,
                    TestUtil.SOURCE_GEOZONE,
                    TestUtil.DESTINATION_GEOZONE,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION));
    Assertions.assertEquals("Transit data not found with given details", exception.getMessage());
    verify(transitPersistenceService, times(1))
        .filterAndGetTransitDetails(any(), any(), any(), any(), any());
  }

  @Test
  void getDistinctDFSATest() throws TransitDomainException {
    when(transitPersistenceService.fetchDestinationGeozones(any(), any(), any()))
        .thenReturn(List.of("B1P", "M1R", "A1F"));
    List<String> dFSAs =
        transitService.getDistinctDFSA(
            TestUtil.ORG_ID, TestUtil.SOURCE_GEOZONE, List.of(TestUtil.CARRIER_SERVICE_ID));
    Assertions.assertEquals(3, dFSAs.size());
    verify(transitPersistenceService, times(1)).fetchDestinationGeozones(any(), any(), anyList());
  }

  @Test
  void getDistinctDFSAExceptionTest() throws TransitDomainException {
    when(transitPersistenceService.fetchDestinationGeozones(any(), any(), any()))
        .thenThrow(
            new TransitDomainException(
                "Failure while fetching DFSAs",
                TestUtil.ORG_ID,
                TestUtil.SOURCE_GEOZONE,
                null,
                null));
    TransitDomainException e =
        Assertions.assertThrows(
            TransitDomainException.class,
            () ->
                transitService.getDistinctDFSA(
                    TestUtil.ORG_ID,
                    TestUtil.SOURCE_GEOZONE,
                    List.of(TestUtil.CARRIER_SERVICE_ID)));

    Assertions.assertEquals(TestUtil.ORG_ID, e.getOrgId());
    Assertions.assertEquals(TestUtil.SOURCE_GEOZONE, e.getSourceGeozone());
    verify(transitPersistenceService, times(1)).fetchDestinationGeozones(any(), any(), anyList());
  }

  @Test
  void deleteTransitDetailsTest() throws TransitDomainException, CommonServiceException {
    TransitDomainDto transitDomainDto2 = testUtil.getTransitDomainDto2(TestUtil.TRANSIT_DAYS);
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(Optional.of(transitDomainDto2));

    TransitResponse transitResponse =
        transitService.deleteTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID);
    Assertions.assertEquals(testUtil.getTransitResponse2(TestUtil.TRANSIT_DAYS), transitResponse);
    verify(transitPersistenceService, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  void deleteTransitDetailsTestException() throws TransitDomainException {
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitService.deleteTransitDetails(
                    TestUtil.ORG_ID,
                    TestUtil.SOURCE_GEOZONE,
                    TestUtil.DESTINATION_GEOZONE,
                    TestUtil.CARRIER_SERVICE_ID));
    Assertions.assertEquals("Transit data not found with given details", exception.getMessage());
    verify(transitPersistenceService, times(1)).findTransitDetails(any(), any(), any(), any());
  }

  @Test
  void getListOfTransitDetailsTest() throws TransitDomainException {
    TransitDomainDto transitEntity = testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS);
    when(transitPersistenceService.fetchTransitList(any(), any(), any()))
        .thenReturn(List.of(transitEntity));

    List<TransitResponse> transitResponse =
        transitService.getListOfTransitDetails(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE, List.of(TestUtil.SOURCE_GEOZONE));
    Assertions.assertEquals(
        transitEntity.getTransitDays(), transitResponse.get(0).getTransitDays());
    verify(transitPersistenceService, times(1)).fetchTransitList(any(), any(), any());
  }

  @Test
  void getTransitTimeEntriesTest() throws TransitDomainException {
    when(transitPersistenceService.fetchTransitEntitiesCount(any(), any())).thenReturn(5);

    TransitTimeEntriesDto transitResponse =
        transitService.getTransitTimeEntries(TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertEquals(TestUtil.ORG_ID, transitResponse.getOrgId());
    Assertions.assertEquals(5, transitResponse.getTotalRecords());

    verify(transitPersistenceService, times(1)).fetchTransitEntitiesCount(any(), any());
  }

  @Test
  void getTransitTimeZeroEntriesTest() throws TransitDomainException {
    when(transitPersistenceService.fetchTransitEntitiesCount(any(), any())).thenReturn(0);

    TransitTimeEntriesDto transitResponse =
        transitService.getTransitTimeEntries(TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertEquals(TestUtil.ORG_ID, transitResponse.getOrgId());
    Assertions.assertEquals(0, transitResponse.getTotalRecords());

    verify(transitPersistenceService, times(1)).fetchTransitEntitiesCount(any(), any());
  }

  @Test
  void getListOfTransitDetailsForDestinationGeoZoneTest()
      throws TransitDomainException, CommonServiceException {
    TransitDomainDto transitDomainDto = testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS);
    when(transitPersistenceService.fetchTransitListForDestinationGeoZone(any(), any()))
        .thenReturn(List.of(transitDomainDto));
    List<TransitBufferDomainDto> transitBufferDomainDtos =
        List.of(testUtil.getTransitBufferDomainDto(TestUtil.ORG_ID));
    when(transitBufferPersistenceService.findByOrgIdAndDestinationGeozone(any(), any()))
        .thenReturn(transitBufferDomainDtos);
    List<TransitResponse> transitResponse =
        transitService.getListOfTransitDetailsForDestinationGeoZone(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE);
    Assertions.assertEquals(
        transitDomainDto.getTransitDays(), transitResponse.get(0).getTransitDays());
    verify(transitPersistenceService, times(1)).fetchTransitListForDestinationGeoZone(any(), any());
    verify(transitBufferPersistenceService, times(1))
        .findByOrgIdAndDestinationGeozone(any(), any());
  }

  @Test
  void getListOfTransitDetailsForDestinationGeoZoneTest2()
      throws TransitDomainException, CommonServiceException {
    TransitDomainDto transitEntity = testUtil.getTransitDomainDto(TestUtil.TRANSIT_DAYS);
    when(transitPersistenceService.fetchTransitListForDestinationGeoZone(any(), any()))
        .thenReturn(List.of(transitEntity));
    when(transitBufferPersistenceService.findByOrgIdAndDestinationGeozone(any(), any()))
        .thenReturn(new ArrayList<>());

    List<TransitResponse> transitResponse =
        transitService.getListOfTransitDetailsForDestinationGeoZone(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE);
    Assertions.assertEquals(
        transitEntity.getTransitDays(), transitResponse.get(0).getTransitDays());
    verify(transitPersistenceService, times(1)).fetchTransitListForDestinationGeoZone(any(), any());
    verify(transitBufferPersistenceService, times(1))
        .findByOrgIdAndDestinationGeozone(any(), any());
  }

  @Test
  void getTransitDetailsForDestinationGeoZoneTestException() throws TransitDomainException {
    List<TransitDomainDto> transitDomainDtos = Collections.emptyList();
    when(transitPersistenceService.fetchTransitListForDestinationGeoZone(any(), any()))
        .thenReturn(transitDomainDtos);
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitService.getListOfTransitDetailsForDestinationGeoZone(
                    TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE));
    Assertions.assertEquals("Transit data not found with given details", exception.getMessage());
    verify(transitPersistenceService, times(1)).fetchTransitListForDestinationGeoZone(any(), any());
  }

  @Test
  void getTransitDetailsForDestinationGeozones() throws TransitDomainException {
    when(transitPersistenceService.fetchTransitListForDestinationGeoZones(any(), any(), any()))
        .thenReturn(List.of(testUtil.getProjectedTransitEntity()));

    List<TransitResponse> responses =
        transitService.getTransitDetailsForDestinationGeozones(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, List.of(TestUtil.DESTINATION_GEOZONE));
    Assertions.assertFalse(CollectionUtils.isEmpty(responses));
    verify(transitPersistenceService, Mockito.times(1))
        .fetchTransitListForDestinationGeoZones(any(), any(), any());
  }

  @Test
  void deleteTransitBufferDays() throws TransitDomainException {
    TransitDomainDto transitDomainDto = testUtil.getTransitDomainDto(5F);
    transitDomainDto.setBufferDays(1D);
    when(transitPersistenceService.findTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID))
        .thenReturn(Optional.of(transitDomainDto));

    when(transitPersistenceService.saveTransitDomainDto(any())).thenReturn(transitDomainDto);

    TransitResponse response =
        transitService.updateTransitBufferDays(
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE);

    assertNotNull(response);
    Assertions.assertEquals(0D, response.getBufferDays());
  }

  @Test
  void deleteNegativeTransitBufferDays() throws TransitDomainException {
    TransitDomainDto transitDomainDto = testUtil.getTransitDomainDto(-5F);
    transitDomainDto.setBufferDays(1D);
    when(transitPersistenceService.findTransitDetails(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID))
        .thenReturn(Optional.of(transitDomainDto));

    when(transitPersistenceService.saveTransitDomainDto(any())).thenReturn(transitDomainDto);

    TransitResponse response =
        transitService.updateTransitBufferDays(
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE);

    assertNotNull(response);
    Assertions.assertEquals(0D, response.getBufferDays());
  }

  @Test
  void deleteTransitBufferDaysZeroTransitBufferDays() throws TransitDomainException {
    TransitDomainDto transitDomainDto = testUtil.getTransitDomainDto(5F);
    transitDomainDto.setBufferDays(0D);
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(Optional.of(transitDomainDto));

    when(transitPersistenceService.saveTransitDomainDto(any())).thenReturn(transitDomainDto);

    TransitResponse response =
        transitService.updateTransitBufferDays(
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE);

    assertNotNull(response);
    Assertions.assertEquals(0, response.getBufferDays());
  }

  @Test
  void deleteTransitBufferDaysNullTransitBufferDays() throws TransitDomainException {
    TransitDomainDto transitDomainDto = testUtil.getTransitDomainDto(5F);
    transitDomainDto.setBufferDays(null);
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(Optional.of(transitDomainDto));

    when(transitPersistenceService.saveTransitDomainDto(any())).thenReturn(transitDomainDto);

    TransitResponse response =
        transitService.updateTransitBufferDays(
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE);

    assertNotNull(response);
    Assertions.assertNull(response.getBufferDays());
  }

  @Test
  void deleteTransitBufferDaysTransitDetailsNotFound() throws TransitDomainException {
    when(transitPersistenceService.findTransitDetails(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    TransitResponse response =
        transitService.updateTransitBufferDays(
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE);

    Assertions.assertNull(response);
  }

  @Test
  void getDistinctSourceAndDestinationGeoZones() throws TransitDomainException {
    when(transitPersistenceService.fetchDistinctSourceGeoZones(any(), any()))
        .thenReturn(List.of(TestUtil.SOURCE_GEOZONE));

    when(transitPersistenceService.fetchDistinctDestinationGeoZones(any(), any()))
        .thenReturn(List.of(TestUtil.DESTINATION_GEOZONE));

    DistinctGeozonesResponse response =
        transitService.getDistinctSourceAndDestinationGeoZones(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);

    assertNotNull(response);
    Assertions.assertFalse(CollectionUtils.isEmpty(response.getDestinationGeozones()));
    Assertions.assertFalse(CollectionUtils.isEmpty(response.getSourceGeozones()));

    verify(transitPersistenceService, times(1)).fetchDistinctSourceGeoZones(any(), any());
    verify(transitPersistenceService, times(1)).fetchDistinctDestinationGeoZones(any(), any());
  }

  @Test
  void getTransitDetailsForDestinationGeoZoneHappyPathTest()
      throws TransitDomainException, CommonServiceException {
    when(transitPersistenceService.fetchTransitListForDestinationGeoZone(any(), any()))
        .thenReturn(List.of(testUtil.getTransitDomainEntities("UPS")));
    List<TransitResponse> transitResponses =
        transitService.getTransitDetailsForDestinationGeoZone(
            TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE);
    Assertions.assertEquals(1, transitResponses.size());
    Assertions.assertEquals("UPS", transitResponses.getFirst().getCarrierServiceId());
  }

  @Test
  void getTransitDetailsForDestinationGeoZoneNotFoundExceptionTest()
      throws TransitDomainException, CommonServiceException {
    when(transitPersistenceService.fetchTransitListForDestinationGeoZone(any(), any()))
        .thenReturn(List.of());
    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitService.getTransitDetailsForDestinationGeoZone(
                    TestUtil.ORG_ID, TestUtil.DESTINATION_GEOZONE));
    Assertions.assertEquals("Transit data not found with given details", e.getMessage());
    Assertions.assertEquals(2, e.getFieldInfo().size());
  }
}
