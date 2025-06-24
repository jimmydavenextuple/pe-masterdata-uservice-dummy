/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.item.domain.inbound.DeleteItemSubstitutionRequest;
import com.nextuple.item.domain.inbound.UpsertItemSubstitutionRequest;
import com.nextuple.item.domain.outbound.ItemSubstitutionResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(
    name = "pe-item-uservice",
    url = "${spring.application.dependencies.item:http://pe-item-uservice:8080/}")
public interface ItemSubstitutionFeign {
  @PostMapping("/item-substitution")
  ResponseEntity<BaseResponse<ItemSubstitutionResponse>> upsertItemSubstitution(
      @Valid @RequestBody UpsertItemSubstitutionRequest upsertItemSubstitutionRequest);

  @GetMapping("/item-substitution/{orgId}/{primaryItemId}/{primaryUom}")
  ResponseEntity<BaseResponse<List<ItemSubstitutionResponse>>> getItemSubstitution(
      @NotBlank @PathVariable(name = "orgId") String orgId,
      @NotBlank @PathVariable(name = "primaryItemId") String primaryItemId,
      @NotBlank @PathVariable(name = "primaryUom") String primaryUom);

  @DeleteMapping("/item-substitution")
  ResponseEntity<BaseResponse<Void>> deleteItemSubstitution(
      @Valid @RequestBody DeleteItemSubstitutionRequest deleteRequest);
}
