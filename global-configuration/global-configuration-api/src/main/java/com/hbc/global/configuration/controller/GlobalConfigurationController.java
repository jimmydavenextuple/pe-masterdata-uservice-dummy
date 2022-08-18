package com.hbc.global.configuration.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.exception.PromiseEngineException;
import com.hbc.common.response.BaseResponse;
import com.hbc.global.configuration.api.domain.dto.GlobalConfigurationDto;
import com.hbc.global.configuration.api.domain.inbound.CreateGlobalConfigurationRequest;
import com.hbc.global.configuration.api.domain.inbound.UpdateGlobalConfigurationRequest;
import com.hbc.global.configuration.service.GlobalConfigurationService;

import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/global-config")
@RequiredArgsConstructor
public class GlobalConfigurationController {
  private static final Logger logger = LoggerFactory.getLogger(GlobalConfigurationController.class);
  private final GlobalConfigurationService globalConfigurationService;

  @GetMapping("/orgId/{orgId}")
  public ResponseEntity<BaseResponse<GlobalConfigurationDto>> fetchValue(
     @PathVariable String orgId , @RequestParam String type , @RequestParam String key ) throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing fetch Weightage request");
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Global Configuration successfully fetched!")
              .payload(globalConfigurationService.fetchValue(orgId,type,key))
              .build());
    } catch (Exception e) {
      logger.error("Failed to process fetch Global request!");
      throw e;
    }
  }


  @PostMapping
  public ResponseEntity<BaseResponse<GlobalConfigurationDto>> createWeightageConfiguration(
      @Valid @RequestBody CreateGlobalConfigurationRequest baseRequest)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing create Weightage Configuration request");
    try {
      var globalConfigurationDto =
              globalConfigurationService.createGlobalConfig(baseRequest);

      return ResponseEntity.ok(
              BaseResponse.builder()
                      .message("Global Configuration successfully created!")
                      .payload(globalConfigurationDto)
                      .build());
    } catch (Exception e) {
      logger.error("Failed to process create Global Configuration request!");
      throw e;
    }
  }

  @PutMapping("/orgId/{orgId}")
  public ResponseEntity<BaseResponse<GlobalConfigurationDto>> updateGlobalConfiguration(
            @PathVariable String orgId , @RequestParam String type , @RequestParam String key,
  @Valid @RequestBody UpdateGlobalConfigurationRequest baseRequest)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing update Global Configuration request");
    try {
      var globalConfigurationDto =
          globalConfigurationService.updateGlobalConfiguration(orgId, type, key, baseRequest);
      logger.info(
          "Response after updation of global configuration :{}", globalConfigurationDto);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Global Configuration successfully updated!")
              .payload(globalConfigurationDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process update Global Configuration request!");
      throw e;
    }
  }

  @DeleteMapping("/orgId/{orgId}")
  public ResponseEntity<BaseResponse<GlobalConfigurationDto>> deleteWeightageConfiguration(
      @NotBlank @PathVariable String orgId,
      @NotBlank @RequestParam String type,
      @NotBlank @RequestParam String key)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing delete Global Configuration request by tenant id");
    try {
      var weightageConfigurationDto =
          globalConfigurationService.deleteGlobalConfiguration(orgId, type, key);
      logger.info(
          "Response after deletion of weightage configuration :{}", weightageConfigurationDto);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Weightage Configuration successfully deleted!")
              .payload(weightageConfigurationDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process delete Global Configuration request!");
      throw e;
    }
  }
}
