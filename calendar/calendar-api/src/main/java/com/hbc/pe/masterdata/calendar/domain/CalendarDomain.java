package com.hbc.pe.masterdata.calendar.domain;

import com.hbc.pe.masterdata.calendar.domain.entity.CalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.repository.CalendarRepository;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarDomain {
  private static final Logger logger = LoggerFactory.getLogger(CalendarDomain.class);
  private final CalendarRepository calendarRepository;

  public CalendarEntity saveCalendarEntity(CalendarEntity calendarEntity)
      throws CalendarDomainException {
    logger.debug("Inside saveCalendarEntity()");
    try {
      return calendarRepository.save(calendarEntity);
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to create calendar",
          e,
          calendarEntity.getCalendarId(),
          calendarEntity.getOrgId(),
          null,
          null);
    }
  }

  public CalendarEntity getCalendar(String orgId, String calendarId)
      throws CalendarDomainException {
    logger.debug("Inside getCalendar()");
    try {
      return calendarRepository.findByCalendarIdAndOrgId(calendarId, orgId);
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch calendar", e, calendarId, orgId, null, null);
    }
  }
}
