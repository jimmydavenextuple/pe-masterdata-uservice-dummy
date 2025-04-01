package com.nextuple.node.domain.outbound;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VendorResponse implements Serializable {

  @Schema(description = "Unique identifier of the vendor", example = "vendor-101")
  String vendorId;

  @Schema(description = "Unique identifier of the organisation", example = "NEXTUPLE_GR")
  String orgId;

  @Schema(description = "Name of the vendor", example = "Store Vendor")
  String vendorName;

  @Schema(description = "Description of the vendor", example = "Store Vendor")
  String vendorDescription;

  @Schema(description = "Type of the vendor", example = "Store")
  String vendorType;
}
