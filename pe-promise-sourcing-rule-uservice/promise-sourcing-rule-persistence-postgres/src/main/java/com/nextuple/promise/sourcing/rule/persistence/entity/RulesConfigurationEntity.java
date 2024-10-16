/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.entity;

import com.nextuple.core.event.listeners.CommonEntityListener;
import com.nextuple.postgres.entity.CommonBaseEntity;
import com.nextuple.promise.sourcing.rule.api.domain.enums.RulesConfigurationModuleNameEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.RulesConfigurationKey;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@EntityListeners(CommonEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@IdClass(RulesConfigurationKey.class)
@SuperBuilder
@Data
@Table(name = "rules_configuration")
public class RulesConfigurationEntity extends CommonBaseEntity {

  @Id
  @Column(name = "org_id")
  private String orgId;

  @Id
  @Column(name = "rule")
  private String rule;

  @Id
  @Column(name = "rule_name")
  private String ruleName;

  @Id
  @Column(name = "module_name")
  @Enumerated(EnumType.STRING)
  private RulesConfigurationModuleNameEnum moduleName;

  @Column(name = "attribute_definition_id")
  private Long attributeDefinitionId;

  @Id
  @Column(name = "scope")
  @Enumerated(EnumType.STRING)
  private SourcingAttributesDefinitionScopeEnum scope;
}
