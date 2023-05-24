/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.domain.entity;

import com.nextuple.core.event.listeners.CommonEntityListener;
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

@Entity
@IdClass(CarrierServicePK.class)
@EntityListeners(CommonEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "carrier_service")
@Builder
public class CarrierServiceEntity {

  @Id
  @Column(name = "org_id")
  private String orgId;

  @Id
  @Column(name = "carrier_id")
  private String carrierId;

  @Id
  @Column(name = "carrier_service_id")
  private String carrierServiceId;

  @Column(name = "carrier_name")
  private String carrierName;

  @Column(name = "service_name")
  private String serviceName;

  @Column(name = "service_options")
  private String serviceOptions;
}
