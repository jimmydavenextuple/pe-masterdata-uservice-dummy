package com.nextuple.weightage.configuration.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.weightage.configuration.api.domain.dto.WeightageCacheKeyDto;
import com.nextuple.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import com.nextuple.weightage.configuration.api.domain.inbound.CreateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.nextuple.weightage.configuration.api.domain.inbound.UpdateWeightageConfigurationRequest;
import com.nextuple.weightage.configuration.service.WeightageConfigurationService;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/weightage")
@RequiredArgsConstructor
public class WeightageConfigurationController {
  private static final Logger logger =
      LoggerFactory.getLogger(WeightageConfigurationController.class);
  private final WeightageConfigurationService weightageConfigurationService;

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

  @GetMapping
  public ResponseEntity<BaseResponse<WeightageConfigurationDto>> getWeightageConfiguration(
      @NotBlank(message = "orgId can't be empty") @RequestParam String orgId,
      @NotBlank(message = "type can't be empty") @RequestParam String type,
      @NotBlank(message = "key can't be empty") @RequestParam String key)
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

  @GetMapping("/{key}")
  public ResponseEntity<BaseResponse<List<WeightageConfigurationDto>>>
      getWeightageConfigurationsByKey(
          @NotBlank(message = "key can't be empty") @PathVariable String key)
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

  @PutMapping
  public ResponseEntity<BaseResponse<WeightageConfigurationDto>> updateWeightageConfiguration(
      @NotBlank(message = "orgId can't be empty") @RequestParam String orgId,
      @NotBlank(message = "type can't be empty") @RequestParam String type,
      @NotBlank(message = "key can't be empty") @RequestParam String key,
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

  @Transactional
  @DeleteMapping
  public ResponseEntity<BaseResponse<WeightageConfigurationDto>> deleteWeightageConfiguration(
      @NotBlank(message = "orgId can't be empty") @RequestParam String orgId,
      @NotBlank(message = "type can't be empty") @RequestParam String type,
      @NotBlank(message = "key can't be empty") @RequestParam String key)
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
  public ResponseEntity<BaseResponse<List<WeightageCacheKeyDto>>> getWeightageCacheKeys(
      @RequestParam Integer limit) throws PromiseEngineException {
    logger.debug("Processing get Weightage Cache Keys");

    var response = weightageConfigurationService.getAllWeightageCacheKeys(limit);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Weightage Cache Keys fetched successfully")
            .payload(response)
            .build());
  }
}
