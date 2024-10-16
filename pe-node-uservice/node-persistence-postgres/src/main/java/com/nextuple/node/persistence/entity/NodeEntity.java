/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.persistence.entity;

import com.nextuple.core.event.listeners.CommonEntityListener;
import com.nextuple.node.persistence.entity.key.NodeKey;
import com.nextuple.postgres.entity.CommonBaseEntity;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

@Entity
@IdClass(NodeKey.class)
@EntityListeners(CommonEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Table(name = "node")
public class NodeEntity extends CommonBaseEntity {

  @Id
  @Column(name = "node_id")
  private String nodeId;

  @Id
  @Column(name = "org_id")
  private String orgId;

  @Column(name = "street")
  private String street;

  @Column(name = "city")
  private String city;

  @Column(name = "state")
  private String state;

  @Column(name = "zip_code")
  private String zipCode;

  @Column(name = "country")
  private String country;

  @Column(name = "latitude")
  private String latitude;

  @Column(name = "longitude")
  private String longitude;

  @Column(name = "timezone")
  private String timezone;

  @Column(name = "start_working_time")
  private String startWorkingTime;

  @Column(name = "last_working_time")
  private String lastWorkingTime;

  @Type(JsonBinaryType.class)
  @Column(columnDefinition = "jsonb", name = "service_option_eligibilities")
  private Map<String, Boolean> serviceOptionEligibilities;

  @Column(name = "ship_to_home")
  private Boolean shipToHome;

  @Column(name = "bopis_eligible")
  private Boolean bopisEligible;

  @Column(name = "node_type")
  private String nodeType;

  @Column(name = "node_labour_tier")
  private String nodeLabourTier;

  @Column(name = "is_active")
  private Boolean isActive;
}
