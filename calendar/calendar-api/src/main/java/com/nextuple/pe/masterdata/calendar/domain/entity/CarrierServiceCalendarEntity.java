package com.nextuple.pe.masterdata.calendar.domain.entity;

import com.nextuple.common.base.BaseEntity;
import com.nextuple.core.event.listeners.CommonEntityListener;
import com.nextuple.pe.masterdata.calendar.domain.entity.primarykey.CarrierServiceCalendarPK;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "carrier_service_calendars")
@IdClass(CarrierServiceCalendarPK.class)
@EntityListeners(CommonEntityListener.class)
public class CarrierServiceCalendarEntity extends BaseEntity {

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
