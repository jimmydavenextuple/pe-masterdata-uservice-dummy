package com.hbc.weightage.configuration.controller;

import com.hbc.common.exception.PromiseEngineException;
import com.hbc.common.response.BaseResponse;
import com.hbc.weightage.configuration.api.domain.dto.WeightageConfigurationDto;
import com.hbc.weightage.configuration.api.domain.inbound.CreateWeightageConfigurationRequest;
import com.hbc.weightage.configuration.api.domain.inbound.FetchWeightageRequest;
import com.hbc.weightage.configuration.api.domain.inbound.UpdateWeightageConfigurationRequest;
import com.hbc.weightage.configuration.service.WeightageConfigurationService;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weightage")
@RequiredArgsConstructor
public class WeightageConfigurationController {
  private static final Logger logger =
      LoggerFactory.getLogger(WeightageConfigurationController.class);
  private final WeightageConfigurationService weightageConfigurationService;

  @PostMapping
  public ResponseEntity<BaseResponse<Map<String, Float>>> fetchWeightage(
      @Valid @RequestBody FetchWeightageRequest baseRequest) throws PromiseEngineException {
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
      throws PromiseEngineException {
    logger.debug("Processing create Weightage Configuration request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Weightage Configuration successfully created!")
              .payload(weightageConfigurationService.createWeightageConfiguration(baseRequest))
              .build());
    } catch (Exception e) {
      logger.error("Failed to process create Weightage Configuration request!");
      throw e;
    }
  }

  @GetMapping
  public ResponseEntity<BaseResponse<WeightageConfigurationDto>> getWeightageConfiguration(
      @NotBlank @RequestParam String orgId,
      @NotBlank @RequestParam String type,
      @NotBlank @RequestParam String key)
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
      getWeightageConfigurationsByKey(@NotBlank @PathVariable String key)
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
      @NotBlank @RequestParam String orgId,
      @NotBlank @RequestParam String type,
      @NotBlank @RequestParam String key,
      @Valid @RequestBody UpdateWeightageConfigurationRequest baseRequest)
      throws PromiseEngineException {
    logger.debug("Processing update Weightage Configuration request by weightageId");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Weightage Configuration successfully updated!")
              .payload(
                  weightageConfigurationService.updateWeightageConfiguration(
                      orgId, type, key, baseRequest))
              .build());
    } catch (Exception e) {
      logger.error("Failed to process update Weightage Configuration request!");
      throw e;
    }
  }

  @Transactional
  @DeleteMapping
  public ResponseEntity<BaseResponse<WeightageConfigurationDto>> deleteWeightageConfiguration(
      @NotBlank @RequestParam String orgId,
      @NotBlank @RequestParam String type,
      @NotBlank @RequestParam String key)
      throws PromiseEngineException {
    logger.debug("Processing delete Weightage Configuration request by tenant id");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Weightage Configuration successfully deleted!")
              .payload(weightageConfigurationService.deleteWeightageConfiguration(orgId, type, key))
              .build());
    } catch (Exception e) {
      logger.error("Failed to process delete Weightage Configuration request!");
      throw e;
    }
  }
}
