/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.persistence.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.calendar.persistence.domain.CarrierServiceCalendarDomainDto;
import com.nextuple.calendar.persistence.entity.CarrierServiceCalendarEntity;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.mapper.CarrierServiceCalendarEntityMapper;
import com.nextuple.calendar.persistence.repository.CarrierServiceCalendarRepository;
import com.nextuple.calendar.persistence.service.CarrierServiceCalendarPersistenceServiceImpl;
import com.nextuple.calendar.persistence.util.TestUtil;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class CarrierServiceCalendarPersistenceServiceImplTest {

  @Mock private CarrierServiceCalendarRepository carrierServiceCalendarRepository;
  @Mock private CarrierServiceCalendarEntityMapper carrierServiceCalendarEntityMapper;
  @InjectMocks private CarrierServiceCalendarPersistenceServiceImpl carrierServiceCalendarDomain;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(
        carrierServiceCalendarDomain, "repository", carrierServiceCalendarRepository);
    ReflectionTestUtils.setField(
        carrierServiceCalendarDomain, "mapper", carrierServiceCalendarEntityMapper);
  }

  @Test
  void saveCalendarEntityTest() throws CalendarDomainException {
    when(carrierServiceCalendarRepository.save(any()))
        .thenReturn(testUtil.getCarrierServiceCalendarEntity());

    when(carrierServiceCalendarEntityMapper.toEntity(any(CarrierServiceCalendarDomainDto.class)))
        .thenReturn(testUtil.getCarrierServiceCalendarEntity());
    when(carrierServiceCalendarEntityMapper.toDomain(any(CarrierServiceCalendarEntity.class)))
        .thenReturn(testUtil.getCarrierServiceCalendarDomainDto());

    CarrierServiceCalendarDomainDto resp =
        carrierServiceCalendarDomain.saveCarrierServiceCalendar(
            testUtil.getCarrierServiceCalendarDomainDto());

    Assertions.assertEquals(TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.getOrgId()));
    Assertions.assertEquals(TestUtil.DESCRIPTION, Objects.requireNonNull(resp.getDescription()));
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID, Objects.requireNonNull(resp.getCarrierServiceId()));
    Assertions.assertEquals(
        TestUtil.SHIPPING_STAGE, Objects.requireNonNull(resp.getShippingStage()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.getEffectiveDate()));
    verify(carrierServiceCalendarRepository, times(1)).save(any());
  }

  @Test
  void saveCalendarEntityExceptionTest() {
    when(carrierServiceCalendarRepository.save(any())).thenThrow(new RuntimeException("error"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () ->
                carrierServiceCalendarDomain.saveCarrierServiceCalendar(
                    testUtil.getCarrierServiceCalendarDomainDto()));

    Assertions.assertEquals("Unable to create carrier service calendar", ex.getMessage());
    Assertions.assertEquals(TestUtil.CALENDAR_ID, ex.getCalendarId());
    verify(carrierServiceCalendarRepository, times(1)).save(any());
  }

  @Test
  void getCarrierServiceCalendarWithServiceOptionTest() throws CalendarDomainException {
    when(carrierServiceCalendarRepository.findCarrierServiceCalendar(any(), any(), any(), any()))
        .thenReturn(
            List.of(
                testUtil.getCarrierServiceCalendarEntity1(),
                testUtil.getCarrierServiceCalendarEntity()));

    when(carrierServiceCalendarEntityMapper.toDomain(anyList()))
        .thenReturn(
            List.of(
                testUtil.getCarrierServiceCalendarDomainDto1(),
                testUtil.getCarrierServiceCalendarDomainDto()));

    List<CarrierServiceCalendarDomainDto> resp =
        carrierServiceCalendarDomain.getCarrierServiceCalendar(
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SERVICE_OPTION,
            TestUtil.ALL_SHIPPING_STAGE);

    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.get(0).getOrgId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE_2, Objects.requireNonNull(resp.get(0).getEffectiveDate()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.get(1).getEffectiveDate()));
    verify(carrierServiceCalendarRepository, times(1))
        .findCarrierServiceCalendar(any(), any(), any(), any());
  }

  @Test
  void getCarrierServiceCalendarWithServiceOptionExceptionTest() {
    when(carrierServiceCalendarRepository.findCarrierServiceCalendar(any(), any(), any(), any()))
        .thenThrow(new RuntimeException("error"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () ->
                carrierServiceCalendarDomain.getCarrierServiceCalendar(
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SERVICE_OPTION,
                    TestUtil.ALL_SHIPPING_STAGE));

    Assertions.assertEquals("Unable to fetch carrier service calendar", ex.getMessage());
    Assertions.assertEquals(TestUtil.CARRIER_SERVICE_ID, ex.getCarrierServiceId());
    verify(carrierServiceCalendarRepository, times(1))
        .findCarrierServiceCalendar(any(), any(), any(), any());
  }

  @Test
  void getCarrierServiceCalendarTest() throws CalendarDomainException {
    when(carrierServiceCalendarRepository.findAllCarrierServiceCalendar(any(), any(), any()))
        .thenReturn(
            List.of(
                testUtil.getCarrierServiceCalendarEntity1(),
                testUtil.getCarrierServiceCalendarEntity()));

    when(carrierServiceCalendarEntityMapper.toDomain(anyList()))
        .thenReturn(
            List.of(
                testUtil.getCarrierServiceCalendarDomainDto1(),
                testUtil.getCarrierServiceCalendarDomainDto()));

    List<CarrierServiceCalendarDomainDto> resp =
        carrierServiceCalendarDomain.getCarrierServiceCalendar(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ALL_SHIPPING_STAGE);

    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.get(0).getOrgId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE_2, Objects.requireNonNull(resp.get(0).getEffectiveDate()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.get(1).getEffectiveDate()));
    verify(carrierServiceCalendarRepository, times(1))
        .findAllCarrierServiceCalendar(any(), any(), any());
  }

  @Test
  void getCarrierServiceCalendarExceptionTest() {
    when(carrierServiceCalendarRepository.findAllCarrierServiceCalendar(any(), any(), any()))
        .thenThrow(new RuntimeException("error"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () ->
                carrierServiceCalendarDomain.getCarrierServiceCalendar(
                    TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ALL_SHIPPING_STAGE));

    Assertions.assertEquals("Unable to fetch carrier service calendar", ex.getMessage());
    Assertions.assertEquals(TestUtil.CARRIER_SERVICE_ID, ex.getCarrierServiceId());
    verify(carrierServiceCalendarRepository, times(1))
        .findAllCarrierServiceCalendar(any(), any(), any());
  }

  @Test
  void getAllCarrierServiceCalendarsTest() throws CalendarDomainException {
    List<CarrierServiceCalendarEntity> carrierServiceCalendarEntities =
        testUtil.getCarrierServiceCalendarEntityList();

    when(carrierServiceCalendarRepository.findAllCarrierServiceCalendarsByLimit(any()))
        .thenReturn(carrierServiceCalendarEntities);

    when(carrierServiceCalendarEntityMapper.toDomain(anyList()))
        .thenReturn(testUtil.getCarrierServiceCalendarDomainDtoList());

    List<CarrierServiceCalendarDomainDto> response =
        carrierServiceCalendarDomain.getAllCarrierServiceCalendars(2);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(
        carrierServiceCalendarEntities.get(0).getCalendarId(), response.get(0).getCalendarId());
    verify(carrierServiceCalendarRepository, times(1)).findAllCarrierServiceCalendarsByLimit(any());
  }

  @Test
  void getAllCarrierServiceCalendarsExceptionTest() {
    when(carrierServiceCalendarRepository.findAllCarrierServiceCalendarsByLimit(any()))
        .thenThrow(new RuntimeException("Unable to fetch all carrier calendars"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () -> carrierServiceCalendarDomain.getAllCarrierServiceCalendars(2));

    Assertions.assertEquals("Unable to fetch all carrier calendars", ex.getMessage());
    verify(carrierServiceCalendarRepository, times(1)).findAllCarrierServiceCalendarsByLimit(any());
  }

  @Test
  void getCarrierServiceCalendarByOrgIdAndCalendarIdTest() throws CalendarDomainException {
    when(carrierServiceCalendarRepository.findCarrierServiceCalendarByCalendarIdAndOrgId(
            any(), any()))
        .thenReturn(
            List.of(
                testUtil.getCarrierServiceCalendarEntity(),
                testUtil.getCarrierServiceCalendarEntity1()));

    when(carrierServiceCalendarEntityMapper.toDomain(anyList()))
        .thenReturn(testUtil.getCarrierServiceCalendarDomainDtoList());
    List<CarrierServiceCalendarDomainDto> response =
        carrierServiceCalendarDomain.getCarrierServiceCalendarByOrgIdAndCalendarId(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(response.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(response.get(0).getOrgId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(response.get(0).getEffectiveDate()));
    verify(carrierServiceCalendarRepository, times(1))
        .findCarrierServiceCalendarByCalendarIdAndOrgId(any(), any());
  }

  @Test
  void getCarrierServiceCalendarByOrgIdAndCalendarIdExceptionTest() {
    when(carrierServiceCalendarRepository.findCarrierServiceCalendarByCalendarIdAndOrgId(
            any(), any()))
        .thenThrow(new RuntimeException("Unable to fetch carrier service calendar list"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () ->
                carrierServiceCalendarDomain.getCarrierServiceCalendarByOrgIdAndCalendarId(
                    TestUtil.CALENDAR_ID, TestUtil.ORG_ID));

    Assertions.assertEquals("Unable to fetch carrier service calendar list", ex.getMessage());
    verify(carrierServiceCalendarRepository, times(1))
        .findCarrierServiceCalendarByCalendarIdAndOrgId(any(), any());
  }
}
