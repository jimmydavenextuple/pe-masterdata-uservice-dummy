package com.hbc.pe.masterdata.calendar.domain.repository;

import com.hbc.pe.masterdata.calendar.domain.entity.CalendarEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepository extends JpaRepository<CalendarEntity, String> {

  CalendarEntity findByCalendarIdAndOrgId(String calendarId, String orgId);

  Optional<CalendarEntity> findCalendarDetailsByCalendarIdAndOrgId(String calendarId, String orgId);
}
