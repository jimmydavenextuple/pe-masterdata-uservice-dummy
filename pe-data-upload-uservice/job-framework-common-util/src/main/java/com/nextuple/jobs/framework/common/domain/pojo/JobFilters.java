/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.domain.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobFilters {
  @Schema(description = "Type of the job.", example = "UPLOAD_COST_DEFINITION")
  private Optional<
          @Pattern(regexp = "^[A-Za-z_]*$", message = "Invalid input provided for jobType.") String>
      jobType = Optional.empty();

  @Schema(description = "No of days", example = "2")
  private Optional<Integer> days = Optional.empty();

  @Schema(description = "Parameter for sorting.", example = "createdDate")
  private Optional<
          @Pattern(regexp = "^[A-Za-z]*$", message = "Invalid input provided for sortBy.") String>
      sortBy = Optional.empty();

  @Schema(description = "Order of sorting.", example = "DESC")
  private Optional<
          @Pattern(regexp = "^[A-Za-z]*$", message = "Invalid input provided for sortOrder.")
          String>
      sortOrder = Optional.empty();

  @Schema(description = "Number of page for pagination.", example = "3")
  private Optional<Integer> pageNo = Optional.empty();

  @Schema(description = "Size of the page.", example = "15")
  private Optional<Integer> pageSize = Optional.empty();
}
