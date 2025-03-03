/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.domain.entity;

import com.nextuple.core.event.listeners.CommonEntityListener;
import com.nextuple.postgres.entity.CommonBaseEntity;
import com.nextuple.promise.sourcing.rule.api.domain.enums.HolidayCutoffDaysType;
import com.nextuple.promise.sourcing.rule.api.domain.enums.HolidayCutoffStatus;
import com.nextuple.promise.sourcing.rule.domain.primarykeys.HolidayCutoffPK;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@IdClass(HolidayCutoffPK.class)
@Data
@NoArgsConstructor
@SuperBuilder
@Table(
    name = "holiday_cutoff",
    indexes =
        @Index(
            name = "orgId_sourcingAttributesDefinitionId_holidayCutoffRule",
            columnList = "org_id,sourcing_attributes_definition_id,holiday_cutoff_rule"))
@EntityListeners(CommonEntityListener.class)
public class HolidayCutoffEntity extends CommonBaseEntity {

  @Column(name = "org_id")
  @Id
  private String orgId;

  @Column(name = "holiday_cutoff_name")
  @Id
  private String holidayCutoffName;

  @Column(name = "holiday_cutoff_rule")
  @Id
  private String holidayCutoffRule;

  @Column(name = "holiday_cutoff_description")
  private String holidayCutoffDescription;

  @Column(name = "sourcing_attributes_definition_id")
  private Long sourcingAttributesDefinitionId;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private HolidayCutoffStatus status;

  @Column(name = "start_date")
  Date startDate;

  @Column(name = "holiday_cutoff_date")
  Date holidayCutoffDate;

  @Column(name = "holiday_delivery_date")
  Date holidayDeliveryDate;

  @Column(name = "pre_cutoff_days")
  Double preCutoffDays;

  @Column(name = "delivery_cool_down_days")
  Double deliveryCoolDownDays;

  @Column(name = "pre_cutoff_days_type")
  @Enumerated(EnumType.STRING)
  private HolidayCutoffDaysType preCutoffDaysType;

  @Column(name = "delivery_cool_down_days_type")
  @Enumerated(EnumType.STRING)
  private HolidayCutoffDaysType deliveryCoolDownDaysType;
}
