/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.consumer.dto;

import static com.nextuple.transit.consumer.constants.TransitConstants.*;

import com.nextuple.master.data.integration.dto.CommonMasterDataFieldsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TransitFeedDto extends CommonMasterDataFieldsDto implements Serializable {

  private static final long serialVersionUID = -3171243781647168759L;

  @Schema(description = SOURCE_GEOZONE, example = SOURCE_GEOZONE_EXAMPLE)
  private String sourceGeozone;

  @Schema(description = DESTINATION_GEOZONE, example = DESTINATION_GEOZONE_EXAMPLE)
  private String destinationGeozone;

  @Schema(description = CARRIER_SERVICE_ID, example = CARRIER_SERVICE_ID_EXAMPLE)
  private String carrierServiceId;

  @Schema(description = TRANSIT_DAYS, example = TRANSIT_DAYS_EXAMPLE)
  private Float transitDays;
}
