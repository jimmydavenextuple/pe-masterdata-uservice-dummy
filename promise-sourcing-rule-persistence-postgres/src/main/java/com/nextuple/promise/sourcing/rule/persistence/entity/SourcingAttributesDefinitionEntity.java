/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.entity;

import com.nextuple.core.event.listeners.CommonEntityListener;
import com.nextuple.postgres.entity.CommonBaseEntity;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@EntityListeners(CommonEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Table(name = "sourcing_attributes_definition")
public class SourcingAttributesDefinitionEntity extends CommonBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "org_id")
  private String orgId;

  @Column(name = "name")
  private String name;

  @Column(name = "req_attributes")
  private String reqAttributes;

  @Column(name = "opt_attributes")
  private String optAttributes;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private SourcingAttributesDefinitionStatus status;

  @Column(name = "scope")
  @Enumerated(EnumType.STRING)
  private SourcingAttributesDefinitionScopeEnum scope;

  @Column(name = "module")
  private String module;
}
