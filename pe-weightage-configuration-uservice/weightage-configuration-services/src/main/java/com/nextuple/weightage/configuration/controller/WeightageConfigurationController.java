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

/**
 * Controller for managing Weightage Configurations.
 *
 * <p>This class provides endpoints for creating, fetching, updating, and deleting weightage
 * configurations, as well as for retrieving weightage cache keys. The available weightage types
 * include AVAILABILITY, PRIORITY, and PROXIMITY.
 *
 * <p>The controller is tagged with "Weightage Configuration APIs" for easy categorization in API
 * documentation.
 */
@Validated
@RestController
@Tag(name = "Weightage Configuration APIs")
@RequestMapping("/weightage")
@RequiredArgsConstructor
public class WeightageConfigurationController {
  private static final Logger logger =
      LoggerFactory.getLogger(WeightageConfigurationController.class);
  private final WeightageConfigurationService weightageConfigurationService;

  /**
   * Fetches the weightage based on the provided request details.
   *
   * <p>This method processes a POST request to retrieve weightage information using the provided
   * <code>baseRequest</code> body.
   *
   * @param baseRequest The request body containing the parameters required to fetch the weightage.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched weightage
   *     data as a map.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   */
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

  /**
   * Creates a new weightage configuration based on the provided request details.
   *
   * <p>This method processes a POST request to create a new weightage configuration using the
   * provided <code>baseRequest</code> body. It returns a {@link ResponseEntity} containing a {@link
   * BaseResponse} with the created weightage configuration details.
   *
   * @param baseRequest The request body containing the details required to create the weightage
   *     configuration.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the created weightage
   *     configuration.
   * @throws CommonServiceException If a general service exception occurs while processing the
   *     request.
   */
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

  /**
   * Fetches the weightage configuration for a given organization, type, and key.
   *
   * <p>This method processes a GET request to retrieve the weightage configuration based on the
   * provided <code>orgId</code>, <code>type</code>, and <code>key</code> parameters. The weightage
   * configuration can be of type AVAILABILITY, PRIORITY, or PROXIMITY, and is identified by a
   * unique combination of the source, geozone, line, order, and priority.
   *
   * @param orgId The unique identifier of the organization requesting the weightage configuration.
   * @param type The type of weightage configuration. It could be AVAILABILITY, PRIORITY, or
   *     PROXIMITY.
   * @param key The unique combination of source, geozone, line, order, and priority identifying the
   *     configuration.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched weightage
   *     configuration.
   * @throws PromiseEngineException If an error occurs while fetching the configuration.
   */
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

  /**
   * Fetches the list of weightage configurations based on the provided key.
   *
   * <p>This method processes a GET request to retrieve the weightage configurations associated with
   * the given <code>key</code>. The key is a unique combination of source, geozone, line, order,
   * and priority, which identifies the set of configurations.
   *
   * @param key The unique combination of source, geozone, line, order, and priority identifying the
   *     weightage configurations.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with a list of the fetched
   *     weightage configurations.
   * @throws PromiseEngineException If an error occurs while fetching the configurations.
   */
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

  /**
   * Updates the weightage configuration based on the provided organization ID, type, key, and
   * request details.
   *
   * <p>This method processes a PUT request to update an existing weightage configuration. It
   * requires the organization ID, type, key, and request body containing the details of the
   * configuration to be updated.
   *
   * @param orgId The unique identifier of the organization. Cannot be empty.
   * @param type The weightage type of the configuration, such as AVAILABILITY, PRIORITY, or
   *     PROXIMITY.
   * @param key The combination of the source, geozone, line, order, and priority that uniquely
   *     identifies the configuration.
   * @param baseRequest The request body containing the updated weightage configuration details.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated weightage
   *     configuration.
   * @throws PromiseEngineException If an error occurs during the update process.
   */
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

  /**
   * Deletes a weightage configuration based on the provided organization ID, type, and key.
   *
   * <p>This method processes a DELETE request to remove an existing weightage configuration. It
   * requires the organization ID, type, and key to identify the configuration to be deleted.
   *
   * @param orgId The unique identifier of the organization. Cannot be empty.
   * @param type The weightage type of the configuration, such as AVAILABILITY, PRIORITY, or
   *     PROXIMITY.
   * @param key The combination of the source, geozone, line, order, and priority that uniquely
   *     identifies the configuration.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the deleted weightage
   *     configuration.
   * @throws PromiseEngineException If an error occurs during the deletion process.
   */
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

  /**
   * Retrieves a list of weightage cache keys.
   *
   * <p>This method processes a GET request to fetch weightage cache keys, with an optional limit on
   * the number of keys to be retrieved. The limit parameter defines the maximum number of keys to
   * return.
   *
   * @param limit The maximum number of cache keys to retrieve. If null, all keys will be fetched.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of fetched
   *     {@link WeightageCacheKeyDto} objects.
   * @throws PromiseEngineException If an error occurs during the process of fetching the cache
   *     keys.
   */
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
