/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.domain.entity;

import com.nextuple.common.base.CommonBaseEntity;
import com.nextuple.core.event.listeners.CommonEntityListener;
import com.nextuple.sourcing.cost.config.enums.LookupContextEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(CommonEntityListener.class)
@Table(
    name = "cost_attribute_details",
    indexes = @Index(name = "cost_attribute_id_attribute_name", columnList = "id,attribute_name"))
public class CostAttributeDetailsEntity extends CommonBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "attribute_name")
  private String attributeName;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "attribute_description")
  private String attributeDescription;

  @Column(name = "is_published")
  private Boolean isPublished;

  @Column(name = "path")
  private String path;

  @Column(name = "lookup_context")
  private LookupContextEnum lookupContext;
}
