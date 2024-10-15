/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.persistence.entity;

import com.nextuple.core.event.listeners.CommonEntityListener;
import com.nextuple.postal.code.timezone.persistence.entity.key.PostalCodeKey;
import com.nextuple.postgres.entity.CommonBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@IdClass(PostalCodeKey.class)
@EntityListeners(CommonEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Table(
    name = "postal_code",
    indexes = {
      @Index(name = "orgId_zipCodeId", columnList = "org_Id,zip_code"),
      @Index(name = "orgId_zipCodePrefix", columnList = "org_Id,zip_code_prefix")
    })
public class PostalCodeEntity extends CommonBaseEntity {

  @Id
  @Column(name = "org_id")
  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @Id
  @Column(name = "zip_code")
  @Schema(description = "Zip code of the source or destination node.", example = "T2PS2K")
  private String zipCode;

  @Column(name = "zip_code_prefix")
  @Schema(description = "First three characters of the zip code.", example = "T2P")
  private String zipCodePrefix;

  @Column(name = "country")
  @Schema(description = "Country of the zip code.", example = "CA")
  private String country;

  @Column(name = "state")
  @Schema(description = "State of the zip code.", example = "Toronto")
  private String state;

  @Column(name = "city")
  @Schema(description = "City of the zip code.", example = "Ontario")
  private String city;

  @Column(name = "latitude")
  @Schema(description = "Latitude of the zip code.", example = "23.21313")
  private String latitude;

  @Column(name = "longitude")
  @Schema(description = "Longitude of the zip code.", example = "74.12132")
  private String longitude;

  @Column(name = "timezone")
  @Schema(description = "Timezone of the zip code.", example = "America/Toronto")
  private String timeZone;

  @Column(name = "custom_region")
  @Schema(description = "Custom region associated to the given zip code.", example = "CR1")
  private String customRegion;
}
