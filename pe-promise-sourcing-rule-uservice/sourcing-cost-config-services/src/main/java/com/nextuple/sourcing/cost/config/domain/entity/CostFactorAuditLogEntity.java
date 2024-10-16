/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.domain.entity;

import com.nextuple.sourcing.cost.config.enums.CostFactorTypeEnum;
import com.nextuple.sourcing.cost.config.enums.DataTypeEnum;
import com.nextuple.sourcing.cost.config.enums.ExpressionLibraryEnum;
import com.nextuple.sourcing.cost.config.enums.LevelAppliedEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(
    name = "cost_factor_audit_log",
    indexes = @Index(name = "cost_factor_audit_log_id_orgId", columnList = "id,org_id"))
public class CostFactorAuditLogEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "org_id")
  private String orgId;

  @Column(name = "cost_factor")
  private String costFactor;

  @Column(name = "data_type")
  private DataTypeEnum dataType;

  @Column(name = "formula")
  private String formula;

  @Column(name = "library")
  private ExpressionLibraryEnum library;

  @Column(name = "cost_factor_type")
  private CostFactorTypeEnum costFactorType;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "values", columnDefinition = "text", length = 2048)
  private String values;

  @Column(name = "default_value")
  private String defaultValue;

  @Column(name = "level_applied")
  private LevelAppliedEnum levelApplied;

  @Column(name = "uom")
  private String uom;

  @Column(name = "is_bucketed")
  private Boolean isBucketed;

  @Column(name = "timestamp")
  private Date timestamp;
}
