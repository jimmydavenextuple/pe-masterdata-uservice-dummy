package com.nextuple.pe.masterdata.calendar.domain.entity;

import com.nextuple.pe.masterdata.calendar.domain.entity.primarykey.CarrierServiceCalendarPK;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "carrier_service_calendars")
@IdClass(CarrierServiceCalendarPK.class)
public class CarrierServiceCalendarEntity {

  @Id
  @Column(name = "calendar_id")
  private String calendarId;

  @Id
  @Column(name = "org_id")
  private String orgId;

  @Id
  @Column(name = "carrier_service_id")
  private String carrierServiceId;

  @Id
  @Column(name = "shipping_stage")
  private String shippingStage;

  @Id
  @Column(name = "effective_date")
  private String effectiveDate;

  private String description;
}
