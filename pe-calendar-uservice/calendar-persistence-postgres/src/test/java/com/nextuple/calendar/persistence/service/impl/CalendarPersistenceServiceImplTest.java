/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.persistence.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.calendar.persistence.domain.CalendarDomainDto;
import com.nextuple.calendar.persistence.entity.CalendarEntity;
import com.nextuple.calendar.persistence.exception.CalendarDomainException;
import com.nextuple.calendar.persistence.mapper.CalendarEntityMapper;
import com.nextuple.calendar.persistence.repository.CalendarRepository;
import com.nextuple.calendar.persistence.service.CalendarPersistenceServiceImpl;
import com.nextuple.calendar.persistence.util.TestUtil;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

class CalendarPersistenceServiceImplTest {

  @Mock private CalendarRepository calendarRepository;
  @Mock private CalendarEntityMapper calendarEntityMapper;
  @InjectMocks private CalendarPersistenceServiceImpl calendarPersistenceServiceImpl;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(calendarPersistenceServiceImpl, "repository", calendarRepository);
    ReflectionTestUtils.setField(calendarPersistenceServiceImpl, "mapper", calendarEntityMapper);
  }

  @Test
  void saveCalendarEntityTest() throws CalendarDomainException {
    when(calendarRepository.save(any())).thenReturn(testUtil.getCalendarEntity());
    when(calendarEntityMapper.toDomain(any(CalendarEntity.class)))
        .thenReturn(testUtil.getCalendarDomainDto());
    when(calendarEntityMapper.toEntity(any(CalendarDomainDto.class)))
        .thenReturn(testUtil.getCalendarEntity());
    CalendarDomainDto resp =
        calendarPersistenceServiceImpl.saveCalendar(testUtil.getCalendarDomainDto());

    Assertions.assertEquals(TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.getOrgId()));
    Assertions.assertEquals(TestUtil.DESCRIPTION, Objects.requireNonNull(resp.getDescription()));
    Assertions.assertEquals(Boolean.TRUE, Objects.requireNonNull(resp.getIsMondayWorking()));
    Assertions.assertEquals(TestUtil.EXCEPTION_DATE, resp.getExceptionDays().get(0).getDate());
    verify(calendarRepository, times(1)).save(any());
  }

  @Test
  void saveCalendarEntityExceptionTest() {
    when(calendarRepository.save(any())).thenThrow(new RuntimeException("error"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () -> calendarPersistenceServiceImpl.saveCalendar(testUtil.getCalendarDomainDto()));

    Assertions.assertEquals("Unable to create calendar", ex.getMessage());
    Assertions.assertEquals(TestUtil.CALENDAR_ID, ex.getCalendarId());
    verify(calendarRepository, times(1)).save(any());
  }

  @Test
  void getCalendarTest() throws CalendarDomainException {
    when(calendarRepository.findByCalendarIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getCalendarEntity());
    when(calendarEntityMapper.toEntity(any(CalendarDomainDto.class)))
        .thenReturn(testUtil.getCalendarEntity());
    when(calendarEntityMapper.toDomain(any(CalendarEntity.class)))
        .thenReturn(testUtil.getCalendarDomainDto());

    CalendarDomainDto resp =
        calendarPersistenceServiceImpl.getCalendar(TestUtil.ORG_ID, TestUtil.CALENDAR_ID);

    Assertions.assertEquals(TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.getOrgId()));
    Assertions.assertEquals(TestUtil.DESCRIPTION, Objects.requireNonNull(resp.getDescription()));
    Assertions.assertEquals(Boolean.TRUE, Objects.requireNonNull(resp.getIsMondayWorking()));
    Assertions.assertEquals(TestUtil.EXCEPTION_DATE, resp.getExceptionDays().get(0).getDate());
    verify(calendarRepository, times(1)).findByCalendarIdAndOrgId(any(), any());
  }

  @Test
  void getCalendarExceptionTest() {
    when(calendarRepository.findByCalendarIdAndOrgId(any(), any()))
        .thenThrow(new RuntimeException("error"));

    CalendarDomainException ex =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () ->
                calendarPersistenceServiceImpl.getCalendar(TestUtil.ORG_ID, TestUtil.CALENDAR_ID));

    Assertions.assertEquals("Unable to fetch calendar", ex.getMessage());
    Assertions.assertEquals(TestUtil.CALENDAR_ID, ex.getCalendarId());
    verify(calendarRepository, times(1)).findByCalendarIdAndOrgId(any(), any());
  }

  @Test
  void findCalendarListByOrgIdTest() throws CalendarDomainException {
    List<CalendarEntity> calendarEntityList = testUtil.getCalendarEntityList();
    Pageable pageable = PageRequest.of(1, 1);
    Page<CalendarEntity> calendarEntityPage =
        new PageImpl<>(calendarEntityList, pageable, calendarEntityList.size());

    when(calendarRepository.findAllCalendarsByOrgId(anyString(), any(Pageable.class)))
        .thenReturn(calendarEntityPage);
    when(calendarEntityMapper.toDomain(any(CalendarEntity.class)))
        .thenReturn(testUtil.getCalendarDomainDto1())
        .thenReturn(testUtil.getCalendarDomainDto());
    Page<CalendarDomainDto> response =
        calendarPersistenceServiceImpl.findCalendarListByOrgId(
            TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, TestUtil.SORT_ORDER_DESC);

    Assertions.assertEquals(calendarEntityList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(
        calendarEntityList.get(0).getOrgId(), response.getContent().get(0).getOrgId());
    Assertions.assertEquals(
        calendarEntityList.get(0).getCalendarId(), response.getContent().get(0).getCalendarId());
    Assertions.assertEquals(2, response.getTotalElements());

    verify(calendarRepository, times(1)).findAllCalendarsByOrgId(anyString(), any(Pageable.class));
  }

  @Test
  void findCalendarListByOrgIdExceptionTest() throws CalendarDomainException {
    when(calendarRepository.findAllCalendarsByOrgId(anyString(), any(Pageable.class)))
        .thenThrow(new RuntimeException("Unable to fetch calendar list"));

    Exception exception =
        Assertions.assertThrows(
            CalendarDomainException.class,
            () ->
                calendarPersistenceServiceImpl.findCalendarListByOrgId(
                    TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, TestUtil.SORT_ORDER_ASC));
    Assertions.assertEquals("Unable to fetch calendar list", exception.getMessage());
    verify(calendarRepository, times(1)).findAllCalendarsByOrgId(anyString(), any(Pageable.class));
  }
}
