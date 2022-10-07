package com.hbc.pe.masterdata.calendar.domain;

import static com.hbc.common.constants.CommonConstants.DEFAULT_SORT_ORDER;

import com.hbc.calendar.domain.outbound.CalendarResponse;
import com.hbc.pe.masterdata.calendar.domain.entity.CalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.mapper.CalendarMapper;
import com.hbc.pe.masterdata.calendar.domain.repository.CalendarRepository;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarDomain {
  private static final Logger logger = LoggerFactory.getLogger(CalendarDomain.class);
  private static final CalendarMapper INSTANCE = Mappers.getMapper(CalendarMapper.class);
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

  public Page<CalendarResponse> findCalendarListByOrgId(
      String orgId, Integer pageNo, Integer pageSize, String sortBy, String sortOrder)
      throws CalendarDomainException {
    try {
      Pageable pageable = null;
      if (sortOrder.equalsIgnoreCase(DEFAULT_SORT_ORDER)) {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).ascending());
      } else {
        pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy).descending());
      }
      return calendarRepository
          .findAllCalendarsByOrgId(orgId, pageable)
          .map(INSTANCE::convertToCalendarResponse);
    } catch (Exception e) {
      throw new CalendarDomainException(
          "Unable to fetch calendar list", e, null, orgId, null, null);
    }
  }
}
