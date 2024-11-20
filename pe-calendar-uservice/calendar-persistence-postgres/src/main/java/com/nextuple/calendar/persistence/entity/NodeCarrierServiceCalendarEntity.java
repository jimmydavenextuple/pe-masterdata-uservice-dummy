/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.persistence.entity;

import com.nextuple.calendar.persistence.entity.key.NodeCarrierServiceCalendarKey;
import com.nextuple.core.event.listeners.CommonEntityListener;
import com.nextuple.postgres.entity.CommonBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "node_carrier_service_calendars")
@IdClass(NodeCarrierServiceCalendarKey.class)
@EntityListeners(CommonEntityListener.class)
public class NodeCarrierServiceCalendarEntity extends CommonBaseEntity {

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
