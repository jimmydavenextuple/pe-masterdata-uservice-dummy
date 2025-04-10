/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.consumer.dto;

import com.nextuple.master.data.integration.dto.CommonMasterDataFieldsDto;
import com.nextuple.vendor.domain.VendorConstants;
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
public class VendorFeedDto extends CommonMasterDataFieldsDto implements Serializable {

  private static final long serialVersionUID = 1240891589171888066L;

  @Schema(description = VendorConstants.VENDOR_ID, example = VendorConstants.VENDOR_ID_EXAMPLE)
  private String vendorId;

  @Schema(description = VendorConstants.VENDOR_TYPE, example = VendorConstants.VENDOR_TYPE_EXAMPLE)
  private String vendorType;

  @Schema(description = VendorConstants.VENDOR_DESC, example = VendorConstants.VENDOR_DESC_EXAMPLE)
  private String vendorDescription;
}
