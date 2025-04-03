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
