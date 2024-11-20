/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.weightage.configuration.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.weightage.configuration.api.domain.dto.WeightageCacheKeyDto;
import com.nextuple.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import com.nextuple.weightage.configuration.api.domain.inbound.CreateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.nextuple.weightage.configuration.api.domain.inbound.UpdateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.controller.docs.CreateWeightConfigurationDoc;
import com.nextuple.weightage.configuration.controller.docs.DeleteWeightageConfigurationDoc;
import com.nextuple.weightage.configuration.controller.docs.FetchWeightageDoc;
import com.nextuple.weightage.configuration.controller.docs.GetWeightageCacheKeysDoc;
import com.nextuple.weightage.configuration.controller.docs.GetWeightageConfigurationByKeyDoc;
import com.nextuple.weightage.configuration.controller.docs.GetWeightageConfigurationDoc;
import com.nextuple.weightage.configuration.controller.docs.UpdateWeightageConfigurationDoc;
import com.nextuple.weightage.configuration.service.WeightageConfigurationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@Tag(name = "Weightage Configuration APIs")
@RequestMapping("/weightage")
@RequiredArgsConstructor
public class WeightageConfigurationController {
  private static final Logger logger =
      LoggerFactory.getLogger(WeightageConfigurationController.class);
  private final WeightageConfigurationService weightageConfigurationService;

  @FetchWeightageDoc
  @PostMapping
  public ResponseEntity<BaseResponse<Map<String, Float>>> fetchWeightage(
      @Valid @RequestBody FetchWeightageRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing fetch Weightage request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Weightage successfully fetched!")
              .payload(weightageConfigurationService.fetchWeightage(baseRequest))
              .build());
    } catch (Exception e) {
      logger.error("Failed to process fetch Weightage request!");
      throw e;
    }
  }

  @CreateWeightConfigurationDoc
  @PostMapping("/create")
  public ResponseEntity<BaseResponse<WeightageConfigurationDto>> createWeightageConfiguration(
      @Valid @RequestBody CreateWeightageConfigurationRequest baseRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing create Weightage Configuration request");
    try {
      var weightageConfigurationDto =
          weightageConfigurationService.createWeightageConfiguration(baseRequest);
      logger.info(
          "Response after addition of weightage configuration :{}", weightageConfigurationDto);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Weightage Configuration successfully created!")
              .payload(weightageConfigurationDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process create Weightage Configuration request!");
      throw e;
    }
  }

  @GetWeightageConfigurationDoc
  @GetMapping
  public ResponseEntity<BaseResponse<WeightageConfigurationDto>> getWeightageConfiguration(
      @NotBlank(message = "orgId can't be empty")
          @RequestParam
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "type can't be empty")
          @RequestParam
          @Parameter(
              description =
                  "Weightage type of the configuration. Weightage configuration could be AVAILABILITY, PRIORITY or PROXIMITY.",
              example = "PROXIMITY")
          String type,
      @NotBlank(message = "key can't be empty")
          @RequestParam
          @Parameter(
              description = "Combination of the source, geozone, line, order and priority.",
              example = "ONON")
          String key)
      throws PromiseEngineException {
    logger.debug("Processing get Weightage Configuration by weightageId request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Weightage Configuration successfully fetched!")
              .payload(weightageConfigurationService.getWeightageConfiguration(orgId, type, key))
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get Weightage Configuration request!");
      throw e;
    }
  }

  @GetWeightageConfigurationByKeyDoc
  @GetMapping("/{key}")
  public ResponseEntity<BaseResponse<List<WeightageConfigurationDto>>>
      getWeightageConfigurationsByKey(
          @NotBlank(message = "key can't be empty")
              @PathVariable
              @Parameter(
                  description = "Combination of the source, geozone, line, order and priority.",
                  example = "ONON")
              String key)
          throws PromiseEngineException {
    logger.debug("Processing get Weightage Configuration by weightageId request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Weightage Configuration successfully fetched!")
              .payload(weightageConfigurationService.getWeightageConfigurationsByKey(key))
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get Weightage Configuration request!");
      throw e;
    }
  }

  @UpdateWeightageConfigurationDoc
  @PutMapping
  public ResponseEntity<BaseResponse<WeightageConfigurationDto>> updateWeightageConfiguration(
      @NotBlank(message = "orgId can't be empty")
          @RequestParam
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "type can't be empty")
          @RequestParam
          @Parameter(
              description =
                  "Weightage type of the configuration. Weightage configuration could be AVAILABILITY, PRIORITY or PROXIMITY.",
              example = "PROXIMITY")
          String type,
      @NotBlank(message = "key can't be empty")
          @RequestParam
          @Parameter(
              description = "Combination of the source, geozone, line, order and priority.",
              example = "ONON")
          String key,
      @Valid @RequestBody UpdateWeightageConfigurationRequest baseRequest)
      throws PromiseEngineException {
    logger.debug("Processing update Weightage Configuration request by weightageId");
    try {
      var weightageConfigurationDto =
          weightageConfigurationService.updateWeightageConfiguration(orgId, type, key, baseRequest);
      logger.info(
          "Response after updation of weightage configuration :{}", weightageConfigurationDto);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Weightage Configuration successfully updated!")
              .payload(weightageConfigurationDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process update Weightage Configuration request!");
      throw e;
    }
  }

  @DeleteWeightageConfigurationDoc
  @Transactional
  @DeleteMapping
  public ResponseEntity<BaseResponse<WeightageConfigurationDto>> deleteWeightageConfiguration(
      @NotBlank(message = "orgId can't be empty")
          @RequestParam
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "type can't be empty")
          @RequestParam
          @Parameter(
              description =
                  "Weightage type of the configuration. Weightage configuration could be AVAILABILITY, PRIORITY or PROXIMITY.",
              example = "PROXIMITY")
          String type,
      @NotBlank(message = "key can't be empty")
          @RequestParam
          @Parameter(
              description = "Combination of the source, geozone, line, order and priority.",
              example = "ONON")
          String key)
      throws PromiseEngineException {
    logger.debug("Processing delete Weightage Configuration request by tenant id");
    try {
      var weightageConfigurationDto =
          weightageConfigurationService.deleteWeightageConfiguration(orgId, type, key);
      logger.info(
          "Response after deletion of weightage configuration :{}", weightageConfigurationDto);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Weightage Configuration successfully deleted!")
              .payload(weightageConfigurationDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process delete Weightage Configuration request!");
      throw e;
    }
  }

  @GetMapping("/get-all-cache-keys")
  @GetWeightageCacheKeysDoc
  public ResponseEntity<BaseResponse<List<WeightageCacheKeyDto>>> getWeightageCacheKeys(
      @RequestParam @Parameter(description = "Limit of cache keys") Integer limit)
      throws PromiseEngineException {
    logger.debug("Processing get Weightage Cache Keys");

    var response = weightageConfigurationService.getAllWeightageCacheKeys(limit);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Weightage Cache Keys fetched successfully")
            .payload(response)
            .build());
  }
}
