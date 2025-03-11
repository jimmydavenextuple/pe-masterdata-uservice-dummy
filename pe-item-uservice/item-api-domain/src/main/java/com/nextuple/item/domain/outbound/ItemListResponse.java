package com.nextuple.item.domain.outbound;

import com.nextuple.common.pojo.AdditionalAttributes;
import com.nextuple.item.domain.constants.ItemConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ItemListResponse extends AdditionalAttributes {

  @Schema(description = ItemConstants.ITEM_ID, example = ItemConstants.ITEM_ID_EXAMPLE)
  private String itemId;

  @Schema(description = ItemConstants.ITEM_SOURCE, example = ItemConstants.ITEM_SOURCE_EXAMPLE)
  private String itemSource;

  @Schema(description = ItemConstants.ORG_ID, example = ItemConstants.ORG_ID_EXAMPLE)
  private String orgId;

  @Schema(description = ItemConstants.UOM, example = ItemConstants.UOM_EXAMPLE)
  private String uom;

  @Schema(
      description = ItemConstants.SHORT_DESCRIPTION,
      example = ItemConstants.SHORT_DESCRIPTION_EXAMPLE)
  private String shortDescription;

  @Schema(description = "Service Option Eligible", example = "SDND,EXPRESS")
  private String serviceOptions;

  @Schema(description = ItemConstants.PROCESSING_TIME, example = ItemConstants.TIME_EXAMPLE)
  private Double processingTime;

  @Schema(description = ItemConstants.LEAD_TIME, example = ItemConstants.TIME_EXAMPLE)
  private Long leadTime;

  @Schema(description = ItemConstants.HANDLING_TYPE, example = ItemConstants.HANDLING_TYPE_EXAMPLE)
  private String handlingType;
}
