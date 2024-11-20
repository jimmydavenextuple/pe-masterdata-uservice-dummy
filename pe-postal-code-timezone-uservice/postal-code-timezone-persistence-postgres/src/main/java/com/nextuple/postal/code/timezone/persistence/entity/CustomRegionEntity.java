/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.persistence.entity;

import com.nextuple.core.event.listeners.CommonEntityListener;
import com.nextuple.postal.code.timezone.persistence.entity.key.CustomRegionKey;
import com.nextuple.postgres.entity.CommonBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@IdClass(CustomRegionKey.class)
@EntityListeners(CommonEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Table(
    name = "custom_region",
    indexes = @Index(name = "customRegionId_orgId", columnList = "id, org_Id"))
public class CustomRegionEntity extends CommonBaseEntity {

  @Id
  @Column(name = "id")
  private String id;

  @Id
  @Column(name = "org_Id")
  private String orgId;

  @Column(name = "codes")
  private List<String> codes;

  @Column(name = "name")
  private String customRegionName;

  @Column(name = "description")
  private String customRegionDescription;
}
