/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence.entity;

import com.nextuple.core.event.listeners.CommonEntityListener;
import com.nextuple.postgres.entity.CommonBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Table(
    name = "transit_buffers_data",
    indexes =
        @Index(
            name = "buffers_orgId_dest_src_carrier_index",
            columnList = "org_id,destination_geozone,source_geozone,carrier_service_id"))
@EntityListeners(CommonEntityListener.class)
public class TransitBufferV2Entity extends CommonBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "org_id")
  private String orgId;

  @Column(name = "carrier_service_id")
  private String carrierServiceId;

  @Column(name = "source_geozone")
  private String sourceGeozone;

  @Column(name = "destination_geozone")
  private String destinationGeozone;

  @Column(name = "buffer_days")
  private Double bufferDays;

  @Column(name = "buffer_start_date")
  private Date bufferStartDate;

  @Column(name = "buffer_end_date")
  private Date bufferEndDate;

  @Column(name = "transit_buffer_config_request_id")
  private Long transitBufferConfigRequestId;
}
