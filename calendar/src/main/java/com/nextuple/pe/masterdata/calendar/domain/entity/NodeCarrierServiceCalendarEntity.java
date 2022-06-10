package com.nextuple.pe.masterdata.calendar.domain.entity;

import com.nextuple.pe.masterdata.calendar.domain.entity.primarykey.NodeCarrierServiceCalendarPK;
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
@Table(name = "node_carrier_service_calendars")
@IdClass(NodeCarrierServiceCalendarPK.class)
public class NodeCarrierServiceCalendarEntity {

  @Id
  @Column(name = "calendar_id")
  private String calendarId;

  @Id
  @Column(name = "org_id")
  private String orgId;

  @Id
  @Column(name = "node_id")
  private String nodeId;

  @Id
  @Column(name = "carrier_service_id")
  private String carrierServiceId;

  @Id
  @Column(name = "effective_date")
  private String effectiveDate;

  private String description;
}
