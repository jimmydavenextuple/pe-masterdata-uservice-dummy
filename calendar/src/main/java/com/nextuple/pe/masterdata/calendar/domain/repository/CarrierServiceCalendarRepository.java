package com.nextuple.pe.masterdata.calendar.domain.repository;

import com.nextuple.pe.masterdata.calendar.domain.entity.CarrierServiceCalendarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarrierServiceCalendarRepository
    extends JpaRepository<CarrierServiceCalendarEntity, String> {

  @Query(
      value =
          "SELECT * FROM carrier_service_calendars "
              + "where org_id = ?1 "
              + "AND (carrier_service_id = ?2 OR carrier_service_id = 'ALL' ) "
              + "AND shipping_stage = ?3",
      nativeQuery = true)
  List<CarrierServiceCalendarEntity> findAllCarrierServiceCalendar(
      String orgId, String carrierServiceId, String shippingStage);

  @Query(
      value =
          "SELECT * FROM carrier_service_calendars "
              + "where org_id = ?1 "
              + "AND (carrier_service_id = ?2 OR carrier_service_id = ?3 OR carrier_service_id = 'ALL') "
              + "AND shipping_stage = ?4",
      nativeQuery = true)
  List<CarrierServiceCalendarEntity> findCarrierServiceCalendar(
      String orgId, String carrierServiceId, String carrierServiceOption, String shippingStage);
}
