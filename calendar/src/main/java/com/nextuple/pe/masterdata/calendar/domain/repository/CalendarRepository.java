package com.nextuple.pe.masterdata.calendar.domain.repository;

import com.nextuple.pe.masterdata.calendar.domain.entity.CalendarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository extends JpaRepository<CalendarEntity, String> {

  CalendarEntity findByCalendarIdAndOrgId(String calendarId, String orgId);
}
