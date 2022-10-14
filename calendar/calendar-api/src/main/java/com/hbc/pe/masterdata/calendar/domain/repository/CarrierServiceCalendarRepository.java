package com.hbc.pe.masterdata.calendar.domain.repository;

import com.hbc.pe.masterdata.calendar.domain.entity.CarrierServiceCalendarEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrierServiceCalendarRepository
    extends JpaRepository<CarrierServiceCalendarEntity, String> {

  @Query(
      value =
          "SELECT * FROM carrier_service_calendars "
              + "where org_id = ?1 "
              + "AND (carrier_service_id = ?2 OR carrier_service_id = 'ALL' ) "
              + "AND shipping_stage = ?3 ORDER BY effective_date DESC, created_date DESC",
      nativeQuery = true)
  List<CarrierServiceCalendarEntity> findAllCarrierServiceCalendar(
      String orgId, String carrierServiceId, String shippingStage);

  @Query(
      value =
          "SELECT * FROM carrier_service_calendars "
              + "where org_id = ?1 "
              + "AND (carrier_service_id = ?2 OR carrier_service_id = ?3 OR carrier_service_id = 'ALL') "
              + "AND shipping_stage = ?4 ORDER BY effective_date DESC, created_date DESC",
      nativeQuery = true)
  List<CarrierServiceCalendarEntity> findCarrierServiceCalendar(
      String orgId, String carrierServiceId, String carrierServiceOption, String shippingStage);

  Optional<CarrierServiceCalendarEntity>
      findByCalendarIdAndOrgIdAndCarrierServiceIdAndShippingStageAndEffectiveDate(
          String calendarId,
          String orgId,
          String carrierServiceId,
          String shippingStage,
          String effectiveDate);

  @Query(value = "SELECT * FROM carrier_service_calendars LIMIT ?1", nativeQuery = true)
  List<CarrierServiceCalendarEntity> findAllCarrierServiceCalendarsByLimit(Integer limit);

  List<CarrierServiceCalendarEntity> findCarrierServiceCalendarByCalendarIdAndOrgId(
      String calendarId, String orgId);
}
