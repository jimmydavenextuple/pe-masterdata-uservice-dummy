package com.nextuple.item.substitution.consumer.dto;

import com.nextuple.item.domain.constants.ItemSubstitutionConstants;
import com.nextuple.master.data.integration.dto.CommonMasterDataFieldsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ItemSubstitutionFeedDto extends CommonMasterDataFieldsDto implements Serializable {

    private static final long serialVersionUID = 1240891589171888066L;

    @Schema(
            description = ItemSubstitutionConstants.ORG_ID,
            example = ItemSubstitutionConstants.ORG_ID_EXAMPLE)
    private String orgId;

    @Schema(
            description = ItemSubstitutionConstants.PRIMARY_ITEM_ID,
            example = ItemSubstitutionConstants.PRIMARY_ITEM_ID_EXAMPLE)
    private String primaryItemId;

    @Schema(
            description = ItemSubstitutionConstants.PRIMARY_UOM,
            example = ItemSubstitutionConstants.PRIMARY_UOM_EXAMPLE)
    private String primaryUom;

    @Schema(
            description = ItemSubstitutionConstants.ALTERNATE_ITEM_ID,
            example = ItemSubstitutionConstants.ALTERNATE_ITEM_ID_EXAMPLE)
    private String alternateItemId;

    @Schema(
            description = ItemSubstitutionConstants.ALTERNATE_UOM,
            example = ItemSubstitutionConstants.ALTERNATE_UOM_EXAMPLE)
    private String alternateUom;

    @Schema(
            description = ItemSubstitutionConstants.CONVERSION_FACTOR,
            example = ItemSubstitutionConstants.CONVERSION_FACTOR_EXAMPLE)
    private Integer conversionFactor;

    @Schema(
            description = ItemSubstitutionConstants.PRIORITY,
            example = ItemSubstitutionConstants.PRIORITY_EXAMPLE)
    private Integer priority;
}
