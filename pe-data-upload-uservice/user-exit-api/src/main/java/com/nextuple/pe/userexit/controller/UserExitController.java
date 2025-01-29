package com.nextuple.pe.userexit.controller;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.userexit.domain.dto.UserExitConfigDataDto;
import com.nextuple.common.userexit.domain.dto.UserExitMetaDataDto;
import com.nextuple.pe.userexit.controller.docs.*;
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
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing User Exit operations.
 *
 * <p>This controller provides APIs to create, retrieve, update, and manage user exit metadata and
 * configuration data. It serves as the backend interface for handling user exit configurations
 * associated with applications and services.
 *
 * <p>Tagged under "User Exit APIs" for documentation categorization, this controller ensures
 * seamless management of user exit data with robust exception handling and validation mechanisms.
 */
@RestController
@Tag(name = "UserExit APIs")
@RequestMapping("/user-exit")
@RequiredArgsConstructor
@Tag(name = "User Exit APIs")
public class UserExitController {

  private static final Logger logger = LoggerFactory.getLogger(UserExitController.class);

  private final UserExitDataService userExitDataService;

  /**
   * Fetches the user exit metadata.
   *
   * <p>This method retrieves the metadata associated with a given user exit, application, and
   * service. It returns the metadata in a structured format for further processing or consumption.
   *
   * @param appName The name of the application, such as "PE".
   * @param serviceName The name of the service, such as "SourcingService".
   * @param userExitName The name of the user exit, such as "GetSolutionCostForApiUE".
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the user exit metadata.
   * @throws CommonServiceException If there is an error while fetching the metadata.
   */
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

  /**
   * Fetches the user exit configuration data.
   *
   * <p>This method retrieves the configuration data associated with a given user exit, application,
   * service, and organization. It returns the configuration data in a structured format for further
   * use.
   *
   * @param orgId The unique identifier for the organization.
   * @param appName The name of the application.
   * @param serviceName The name of the service.
   * @param userExitName The name of the user exit.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the user exit
   *     configuration data.
   * @throws CommonServiceException If there is an error while fetching the configuration data.
   */
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

  /**
   * Inserts new configuration data for a user exit.
   *
   * <p>This method accepts a request body containing the configuration data for a user exit and
   * inserts it into the system. It returns a response containing the result of the insertion.
   *
   * @param createConfigDataRequest The request body containing the configuration data to be
   *     inserted.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the inserted
   *     configuration data.
   * @throws CommonServiceException If there is an error while inserting the configuration data.
   */
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

  /**
   * Inserts new metadata for a user exit.
   *
   * <p>This method accepts a request body containing the metadata for a user exit and inserts it
   * into the system. It returns a response containing the result of the insertion.
   *
   * @param createMetaDataRequest The request body containing the metadata to be inserted.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the inserted metadata.
   * @throws CommonServiceException If there is an error while inserting the metadata.
   */
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

  /**
   * Updates the configuration data of an existing user exit.
   *
   * <p>This method updates the configuration data for a specified user exit, application, service,
   * and organization. It returns a response containing the updated configuration data.
   *
   * @param userExitName The unique name of the user exit.
   * @param appName The unique name of the application.
   * @param orgId The unique identifier for the organization.
   * @param serviceName The unique name of the service.
   * @param updateConfigDataRequest The request body containing the updated configuration data.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated
   *     configuration data.
   * @throws CommonServiceException If there is an error while updating the configuration data.
   */
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

  /**
   * Updates the metadata of an existing user exit.
   *
   * <p>This method updates the metadata for a specified user exit, application, and service. It
   * returns a response containing the updated metadata.
   *
   * @param name The unique name of the user exit.
   * @param appName The unique name of the application.
   * @param serviceName The unique name of the service.
   * @param updateMetaDataRequest The request body containing the updated metadata.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated metadata.
   * @throws CommonServiceException If there is an error while updating the metadata.
   */
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
