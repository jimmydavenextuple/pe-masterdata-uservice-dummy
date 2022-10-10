package com.hbc.pe.masterdata.calendar.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.calendar.domain.outbound.CalendarResponse;
import com.hbc.pe.masterdata.calendar.domain.entity.CalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.repository.CalendarRepository;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.util.TestUtil;
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

class CalendarDomainTest {

  @Mock private CalendarRepository calendarRepository;
  @InjectMocks private CalendarDomain calendarDomain;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void saveCalendarEntityTest() throws CalendarDomainException {
    when(calendarRepository.save(any())).thenReturn(testUtil.getCalendarEntity());

    CalendarEntity resp = calendarDomain.saveCalendarEntity(testUtil.getCalendarEntity());

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
            () -> calendarDomain.saveCalendarEntity(testUtil.getCalendarEntity()));

    Assertions.assertEquals("Unable to create calendar", ex.getMessage());
    Assertions.assertEquals(TestUtil.CALENDAR_ID, ex.getCalendarId());
    verify(calendarRepository, times(1)).save(any());
  }

  @Test
  void getCalendarTest() throws CalendarDomainException {
    when(calendarRepository.findByCalendarIdAndOrgId(any(), any()))
        .thenReturn(testUtil.getCalendarEntity());

    CalendarEntity resp = calendarDomain.getCalendar(TestUtil.ORG_ID, TestUtil.CALENDAR_ID);

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
            () -> calendarDomain.getCalendar(TestUtil.ORG_ID, TestUtil.CALENDAR_ID));

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

    Page<CalendarResponse> response =
        calendarDomain.findCalendarListByOrgId(
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
                calendarDomain.findCalendarListByOrgId(
                    TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, TestUtil.SORT_ORDER_ASC));
    Assertions.assertEquals("Unable to fetch calendar list", exception.getMessage());
    verify(calendarRepository, times(1)).findAllCalendarsByOrgId(anyString(), any(Pageable.class));
  }
}
