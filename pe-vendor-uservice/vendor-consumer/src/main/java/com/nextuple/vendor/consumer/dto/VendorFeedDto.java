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
