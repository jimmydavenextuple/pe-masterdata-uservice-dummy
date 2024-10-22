package com.nextuple.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageParams {

  @Schema(description = "Specifies the number of the page.", example = "2")
  private Optional<@Min(1) Integer> pageNo = Optional.empty();

  @Schema(description = "Specifies the size of the page.", example = "15")
  private Optional<@Min(1) Integer> pageSize = Optional.empty();

  @Schema(description = "Specifies the criteria of sorting.", example = "createdDate")
  private Optional<
          @Pattern(regexp = "^[A-Za-z]*$", message = "Invalid input provided for sortBy.") String>
      sortBy = Optional.empty();

  @Schema(description = "Specifies the order of sorting.", example = "ASC")
  private Optional<
          @Pattern(regexp = "^[A-Za-z]*$", message = "Invalid input provided for sortOrder.")
          String>
      sortOrder = Optional.empty();
}
