package com.nextuple.node.domain.inbound;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorUpdationRequest implements Serializable {
  @Schema(description = "Name of the vendor", example = "Store Vendor")
  String vendorName;

  @Schema(description = "Description of the vendor", example = "Store Vendor")
  String vendorDescription;

  @Schema(description = "Type of the vendor", example = "Store")
  String vendorType;
}
