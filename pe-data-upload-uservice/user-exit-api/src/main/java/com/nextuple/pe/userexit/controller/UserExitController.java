package com.nextuple.pe.userexit.controller;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.userexit.domain.dto.UserExitConfigDataDto;
import com.nextuple.common.userexit.domain.dto.UserExitMetaDataDto;
import com.nextuple.pe.userexit.controller.docs.FetchConfigDatadoc;
import com.nextuple.pe.userexit.controller.docs.FetchUserExitMetadataDoc;
import com.nextuple.pe.userexit.controller.docs.InsertConfigDataDoc;
import com.nextuple.pe.userexit.controller.docs.InsertMetadataDoc;
import com.nextuple.pe.userexit.controller.docs.UpdateConfigDataDoc;
import com.nextuple.pe.userexit.controller.docs.UpdateMetadataDoc;
import com.nextuple.pe.userexit.domain.inbound.CreateConfigDataRequest;
import com.nextuple.pe.userexit.domain.inbound.CreateMetaDataRequest;
import com.nextuple.pe.userexit.domain.inbound.UpdateConfigDataRequest;
import com.nextuple.pe.userexit.domain.inbound.UpdateMetaDataRequest;
import com.nextuple.pe.userexit.domain.outbound.ConfigDataResponse;
import com.nextuple.pe.userexit.domain.outbound.MetaDataResponse;
import com.nextuple.pe.userexit.service.UserExitDataService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "UserExit APIs")
@RequestMapping("/user-exit")
@RequiredArgsConstructor
@Tag(name = "User Exit APIs")
public class UserExitController {

  private static final Logger logger = LoggerFactory.getLogger(UserExitController.class);

  private final UserExitDataService userExitDataService;

  @GetMapping("/metadata/{appName}/{serviceName}/{userExitName}")
  @FetchUserExitMetadataDoc
  public ResponseEntity<BaseResponse<UserExitMetaDataDto>> fetchMetaData(
      @NotBlank(message = "App name can't be empty")
          @PathVariable
          @Parameter(description = "Name of the app.", example = "PE")
          String appName,
      @NotBlank(message = "Service name can't be empty")
          @PathVariable
          @Parameter(description = "Name of the service.", example = "SourcingService")
          String serviceName,
      @NotBlank(message = "User Exit name can't be empty")
          @PathVariable
          @Parameter(description = "Name of the user exit.", example = "GetSolutionCostForApiUE")
          String userExitName)
      throws CommonServiceException {
    try {
      UserExitMetaDataDto metaData =
          userExitDataService.fetchMetaData(userExitName, appName, serviceName);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("User Exit metadata fetched successfully")
              .payload(metaData)
              .build());
    } catch (Exception e) {
      logger.error("Error while fetching user exit meta data");
      throw e;
    }
  }

  @GetMapping("/config/{orgId}/{appName}/{serviceName}/{userExitName}")
  @FetchConfigDatadoc
  public ResponseEntity<BaseResponse<UserExitConfigDataDto>> fetchConfigData(
      @NotBlank(message = "Org Id can't be empty") @PathVariable String orgId,
      @NotBlank(message = "App name can't be empty") @PathVariable String appName,
      @NotBlank(message = "Service name can't be empty") @PathVariable String serviceName,
      @NotBlank(message = "User Exit name can't be empty") @PathVariable String userExitName)
      throws CommonServiceException {
    try {
      UserExitConfigDataDto configData =
          userExitDataService.fetchConfigData(userExitName, appName, orgId, serviceName);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("User Exit config data fetched successfully")
              .payload(configData)
              .build());
    } catch (Exception e) {
      logger.error("Error while fetching user exit config data");
      throw e;
    }
  }

  @PostMapping(value = "/configdata")
  @InsertConfigDataDoc
  public ResponseEntity<BaseResponse<ConfigDataResponse>> insertConfigData(
      @Valid @RequestBody CreateConfigDataRequest createConfigDataRequest)
      throws CommonServiceException {
    logger.debug("Processing add config data request");
    var configDataResponse = userExitDataService.addConfigData(createConfigDataRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("User Exit configdata inserted successfully")
            .payload(configDataResponse)
            .build());
  }

  @PostMapping(value = "/metadata")
  @InsertMetadataDoc
  public ResponseEntity<BaseResponse<MetaDataResponse>> insertMetaData(
      @Valid @RequestBody CreateMetaDataRequest createMetaDataRequest)
      throws CommonServiceException {
    logger.debug("Processing add meta data request");
    var metaDataResponse = userExitDataService.addMetaData(createMetaDataRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("User Exit metadata inserted successfully")
            .payload(metaDataResponse)
            .build());
  }

  @PutMapping("/update/config-data/{orgId}/{appName}/{serviceName}/{userExitName}")
  @UpdateConfigDataDoc
  public ResponseEntity<BaseResponse<ConfigDataResponse>> updateConfigData(
      @NotBlank(message = "userExitName can't be empty")
          @PathVariable
          @Parameter(description = "Unique name of the user exit.")
          String userExitName,
      @NotBlank(message = "appName can't be empty")
          @PathVariable
          @Parameter(description = "Unique name of the app.")
          String appName,
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier for the organisation.")
          String orgId,
      @NotBlank(message = "serviceName can't be empty")
          @PathVariable
          @Parameter(description = "Unique name of the service.")
          String serviceName,
      @Valid @RequestBody UpdateConfigDataRequest updateConfigDataRequest)
      throws CommonServiceException {
    logger.debug("Processing update config data request");
    var configDataResponse =
        userExitDataService.updateConfigData(
            userExitName, appName, orgId, serviceName, updateConfigDataRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("User Exit config data updated successfully")
            .payload(configDataResponse)
            .build());
  }

  @PutMapping("/update/meta-data/{name}/{appName}/{serviceName}")
  @UpdateMetadataDoc
  public ResponseEntity<BaseResponse<MetaDataResponse>> updateConfigData(
      @NotBlank(message = "name can't be empty")
          @PathVariable
          @Parameter(description = "Unique name of the user exit.")
          String name,
      @NotBlank(message = "appName can't be empty")
          @PathVariable
          @Parameter(description = "Unique name of the app.")
          String appName,
      @NotBlank(message = "serviceName can't be empty")
          @PathVariable
          @Parameter(description = "Unique name of the service.")
          String serviceName,
      @Valid @RequestBody UpdateMetaDataRequest updateMetaDataRequest)
      throws CommonServiceException {
    logger.debug("Processing update meta data request");
    var metaDataResponse =
        userExitDataService.updateMetaData(name, appName, serviceName, updateMetaDataRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("User Exit meta data updated successfully")
            .payload(metaDataResponse)
            .build());
  }
}
