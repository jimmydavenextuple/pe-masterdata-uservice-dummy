/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postalcodecarrierservice.persistence.entity;

import com.nextuple.postalcodecarrierservice.persistence.entity.key.PostalCodeCarrierServiceKey;
import com.nextuple.postgres.entity.CommonBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@IdClass(PostalCodeCarrierServiceKey.class)
// @EntityListeners(CommonEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Table(
    name = "postal_code_carrier_service",
    indexes = {
      @Index(name = "idx_zipcode_carrier_service", columnList = "zipcode,carrier_service_id"),
      @Index(name = "idx_postal_code_carrier_service_zipcode", columnList = "zipcode"),
      @Index(
          name = "idx_postal_code_carrier_service_carrier_service_id",
          columnList = "carrier_service_id"),
      @Index(name = "idx_postal_code_carrier_service_red_zone", columnList = "is_red_zone")
    })
public class PostalCodeCarrierServiceEntity extends CommonBaseEntity {

  @Id
  @Column(name = "zipcode", nullable = false, length = 20)
  private String zipcode;

  @Id
  @Column(name = "carrier_service_id", nullable = false, length = 100)
  private String carrierServiceId;

  @Column(name = "is_red_zone", nullable = false)
  private Boolean isRedZone = Boolean.FALSE;

  @Column(name = "red_zone_reason", columnDefinition = "TEXT")
  private String redZoneReason;
}
