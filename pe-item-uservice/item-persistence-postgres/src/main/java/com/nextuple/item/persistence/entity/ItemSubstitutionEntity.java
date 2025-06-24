/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.persistence.entity;

import com.nextuple.item.persistence.entity.key.ItemSubstitutionKey;
import com.nextuple.postgres.entity.CommonBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@IdClass(ItemSubstitutionKey.class)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Table(
    name = "item_substitution",
    indexes = {
      @Index(
          name = "idx_item_substitution_primary",
          columnList = "org_id,primary_item_id,primary_uom"),
      @Index(
          name = "idx_item_substitution_full",
          columnList = "org_id,primary_item_id,primary_uom,alternate_item_id,alternate_uom")
    })
public class ItemSubstitutionEntity extends CommonBaseEntity {

  @Id
  @Column(name = "org_id")
  private String orgId;

  @Id
  @Column(name = "primary_item_id")
  private String primaryItemId;

  @Id
  @Column(name = "primary_uom")
  private String primaryUom;

  @Id
  @Column(name = "alternate_item_id")
  private String alternateItemId;

  @Id
  @Column(name = "alternate_uom")
  private String alternateUom;

  @Column(name = "conversion_factor")
  private Integer conversionFactor;

  @Column(name = "priority")
  private Integer priority;
}
