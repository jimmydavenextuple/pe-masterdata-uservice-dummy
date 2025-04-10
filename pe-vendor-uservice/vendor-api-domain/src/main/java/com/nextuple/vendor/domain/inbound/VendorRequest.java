/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.domain.inbound;

import com.nextuple.common.pojo.AdditionalAttributes;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class VendorRequest extends AdditionalAttributes implements Serializable {
  @Schema(description = "Unique identifier of the vendor", example = "vendor-101")
  @NotBlank(message = "vendorId can't be blank")
  String vendorId;

  @Schema(description = "Unique identifier of the organisation", example = "NEXTUPLE_GR")
  @NotBlank(message = "orgId can't be blank")
  @Length(max = 50)
  String orgId;

  @Schema(description = "Description of the vendor", example = "Store Vendor")
  String vendorDescription;

  @Schema(description = "Type of the vendor", example = "Store")
  String vendorType;
}
