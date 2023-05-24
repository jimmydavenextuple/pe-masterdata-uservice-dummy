/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.domain.entity;

import com.nextuple.core.event.listeners.CommonEntityListener;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@IdClass(NodePK.class)
@EntityListeners(CommonEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "node")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class NodeEntity {

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

  @Column(name = "province")
  private String province;

  @Column(name = "postal_code")
  private String postalCode;

  @Column(name = "country")
  private String country;

  @Column(name = "latitude")
  private String latitude;

  @Column(name = "longitude")
  private String longitude;

  @Column(name = "timezone")
  private String timezone;

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb", name = "service_option_eligibilities")
  private Map<String, Boolean> serviceOptionEligibilities;

  @Column(name = "ship_to_home")
  private Boolean shipToHome;

  @Column(name = "bopis_eligible")
  private Boolean bopisEligible;

  @Column(name = "node_type")
  private String nodeType;

  @Column(name = "is_active")
  private Boolean isActive;
}
