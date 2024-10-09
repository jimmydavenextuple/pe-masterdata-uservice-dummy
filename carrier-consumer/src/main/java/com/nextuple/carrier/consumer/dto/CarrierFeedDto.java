/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.consumer.dto;

import static com.nextuple.carrier.consumer.constants.CarrierConstants.*;

import com.nextuple.master.data.integration.dto.CommonMasterDataFieldsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CarrierFeedDto extends CommonMasterDataFieldsDto implements Serializable {
  @Serial private static final long serialVersionUID = 4351941361194695562L;

  @Schema(description = CARRIER_ID, example = CARRIER_ID_EXAMPLE)
  private String carrierId;

  @Schema(description = CARRIER_SERVICE_ID, example = CARRIER_SERVICE_ID_EXAMPLE)
  private String carrierServiceId;

  @Schema(description = CARRIER_NAME, example = CARRIER_NAME_EXAMPLE)
  private String carrierName;

  @Schema(description = SERVICE_NAME, example = SERVICE_NAME_EXAMPLE)
  private String serviceName;

  @Schema(description = SERVICE_OPTIONS, example = SERVICE_OPTIONS_EXAMPLE)
  private String serviceOptions;
}
