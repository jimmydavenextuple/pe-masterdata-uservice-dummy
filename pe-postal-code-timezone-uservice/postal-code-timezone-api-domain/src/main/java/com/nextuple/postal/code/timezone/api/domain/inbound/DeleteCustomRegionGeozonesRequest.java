/*Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
*/

package com.nextuple.postal.code.timezone.api.domain.inbound;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCustomRegionGeozonesRequest implements Serializable {

  private static final long serialVersionUID = 768362501232042475L;

  @NotBlank(message = "id can't be empty")
  @Schema(description = "Unique identifier of the region.", example = "CRID1")
  private String id;

  @NotNull(message = "codes can't be empty.")
  @Schema(
      description = "Array of the zip code prefixes in a custom region.",
      example = "['T2P','T3P']")
  private List<String> codes;
}
