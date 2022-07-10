package com.hbc.pe.masterdata.calendar.domain.entity;

import com.hbc.core.event.listeners.CommonEntityListener;
import com.hbc.pe.masterdata.calendar.domain.entity.primarykey.NodeCalendarPK;
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
@Table(name = "node_calendars")
@IdClass(NodeCalendarPK.class)
@EntityListeners(CommonEntityListener.class)
public class NodeCalendarEntity {

  @Id
  @Column(name = "calendar_id")
  private String calendarId;

  @Id
  @Column(name = "node_id")
  private String nodeId;

  @Id
  @Column(name = "org_id")
  private String orgId;

  @Id
  @Column(name = "effective_date")
  private String effectiveDate;

  private String description;
}
