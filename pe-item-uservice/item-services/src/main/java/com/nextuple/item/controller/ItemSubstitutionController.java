/*
 *  Copyright (c) 2025, Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 *  The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 *  The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.item.domain.constants.ItemSubstitutionConstants;
import com.nextuple.item.domain.inbound.DeleteItemSubstitutionRequest;
import com.nextuple.item.domain.inbound.UpsertItemSubstitutionRequest;
import com.nextuple.item.domain.outbound.ItemSubstitutionResponse;
import com.nextuple.item.service.ItemSubstitutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@Tag(name = "Item Substitution APIs")
@RequestMapping("/item-substitution")
@RequiredArgsConstructor
@Slf4j
public class ItemSubstitutionController {
  private final ItemSubstitutionService itemSubstitutionService;

  @PostMapping
  public ResponseEntity<BaseResponse<ItemSubstitutionResponse>> upsertItemSubstitution(
      @Valid @RequestBody UpsertItemSubstitutionRequest upsertItemSubstitutionRequest) {
    log.debug("Processing item substitution upsert request");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Item substitution successfully upserted")
            .payload(itemSubstitutionService.upsertItemSubstitution(upsertItemSubstitutionRequest))
            .build());
  }

  @GetMapping("/{orgId}/{primaryItemId}/{primaryUom}")
  public ResponseEntity<BaseResponse<List<ItemSubstitutionResponse>>> getItemSubstitution(
      @Parameter(
              description = ItemSubstitutionConstants.ORG_ID,
              example = ItemSubstitutionConstants.ORG_ID_EXAMPLE)
          @PathVariable(name = "orgId")
          String orgId,
      @Parameter(
              description = ItemSubstitutionConstants.PRIMARY_ITEM_ID,
              example = ItemSubstitutionConstants.PRIMARY_ITEM_ID_EXAMPLE)
          @PathVariable(name = "primaryItemId")
          String primaryItemId,
      @Parameter(
              description = ItemSubstitutionConstants.PRIMARY_UOM,
              example = ItemSubstitutionConstants.PRIMARY_UOM_EXAMPLE)
          @PathVariable(name = "primaryUom")
          String primaryUom) {
    log.debug(
        "Getting item substitution for orgId:{}, primaryItemId: {}, primaryUom: {}",
        orgId,
        primaryItemId,
        primaryUom);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Item substitution retrieved successfully")
            .payload(itemSubstitutionService.getItemSubstitution(orgId, primaryItemId, primaryUom))
            .build());
  }

  @DeleteMapping
  @Operation(summary = "Delete an Item Substitution by primary item ID and UOM")
  public ResponseEntity<BaseResponse<Void>> deleteItemSubstitution(
      @Valid @RequestBody DeleteItemSubstitutionRequest deleteRequest) {
    log.debug("Deleting item substitution for deleteRequest: {}", deleteRequest);
    itemSubstitutionService.deleteItemSubstitution(deleteRequest);
    return ResponseEntity.ok(
        BaseResponse.<Void>builder().message("Item substitution deleted successfully").build());
  }
}
