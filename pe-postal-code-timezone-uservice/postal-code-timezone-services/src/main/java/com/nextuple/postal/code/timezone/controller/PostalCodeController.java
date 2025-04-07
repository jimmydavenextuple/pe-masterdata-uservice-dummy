/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.controller;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.postal.code.timezone.api.domain.dto.MarketRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodePrefixDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.PostalCodeRequest;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.postal.code.timezone.controller.docs.CreatePostalCodeDetails;
import com.nextuple.postal.code.timezone.controller.docs.DeletePostalCodeDetails;
import com.nextuple.postal.code.timezone.controller.docs.GetCustomRegionIdDetails;
import com.nextuple.postal.code.timezone.controller.docs.GetMargetRegionsForOrgIdDetails;
import com.nextuple.postal.code.timezone.controller.docs.GetPostalCodeDetails;
import com.nextuple.postal.code.timezone.controller.docs.GetPostalCodeListByPrefix;
import com.nextuple.postal.code.timezone.controller.docs.GetPostalCodePrefixListDetails;
import com.nextuple.postal.code.timezone.controller.docs.GetPostalCodePrefixListForOrgIdAndStateDetails;
import com.nextuple.postal.code.timezone.controller.docs.GetPostalCodeTimezoneForOrgIdAndCountryDetails;
import com.nextuple.postal.code.timezone.controller.docs.UpdatePostalCodeDetails;
import com.nextuple.postal.code.timezone.service.PostalCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing postal codes.
 *
 * <p>This controller provides APIs to create, update, delete, and retrieve postal codes for a
 * specific organization. It also allows retrieval of zip code prefixes, custom regions, and
 * timezone information related to postal codes. Additionally, the controller supports fetching
 * lists of market regions for an organization and handling state-specific postal code prefixes.
 *
 * <p>The controller is tagged with "Zip Code APIs" for easy categorization in API documentation.
 */
@Validated
@RestController
@Tag(name = "Zip Code APIs")
@RequestMapping("/postal-code")
@RequiredArgsConstructor
public class PostalCodeController {

  private static final Logger logger = LoggerFactory.getLogger(PostalCodeController.class);
  private final PostalCodeService postalCodeService;

  /**
   * Creates a zip code with the given details.
   *
   * <p>This method processes a POST request to create a new zip code based on the details provided
   * in the request body.
   *
   * @param postalCodeRequest The request body containing the details for the zip code to be
   *     created.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the created zip code
   *     details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   */
  @Operation(
      summary = "Create Zip Code",
      description = "Creates the zip code with the given details.")
  @CreatePostalCodeDetails
  @PostMapping
  public ResponseEntity<BaseResponse<PostalCodeResponse>> createPostalCode(
      @Valid @RequestBody PostalCodeRequest postalCodeRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing create zip code request");
    try {
      var postalCodeEntity = postalCodeService.createPostalCode(postalCodeRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Zip Code successfully created!")
              .payload(postalCodeEntity)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process create zip code request", e);
      throw e;
    }
  }

  /**
   * Updates a zip code with the given details.
   *
   * <p>This method processes a PUT request to update an existing zip code based on the details
   * provided in the request body.
   *
   * @param postalCodeRequest The request body containing the details for the zip code to be
   *     updated.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated zip code
   *     details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   */
  @Operation(
      summary = "Update Zip Code",
      description = "Updates the zip code with the given details.")
  @UpdatePostalCodeDetails
  @PutMapping("/update")
  public ResponseEntity<BaseResponse<PostalCodeResponse>> updatePostalCode(
      @Valid @RequestBody PostalCodeRequest postalCodeRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing update zip code request");
    try {
      var postalCodeResponse = postalCodeService.updatePostalCode(postalCodeRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Zip Code Details successfully updated")
              .payload(postalCodeResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process update zip code request", e);
      throw e;
    }
  }

  /**
   * Retrieves a zip code by the organization ID and zip code.
   *
   * <p>This method processes a GET request to fetch the details of a zip code identified by the
   * given organization ID and zip code.
   *
   * @param orgId The unique identifier of the organization.
   * @param postalCode The zip code of the source or destination node.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched zip code
   *     details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   */
  @Operation(
      summary = "Get Zip Code",
      description = "Retrieves the zip code by the organization ID and zip code.")
  @GetPostalCodeDetails
  @GetMapping("/{orgId}/{postalCode}")
  public ResponseEntity<BaseResponse<PostalCodeResponse>> getPostalCode(
      @NotNull
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull
          @PathVariable
          @Parameter(
              description = "Zip code of the source or destination node.",
              example = "T2PS2K")
          String postalCode)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing get zip code request");
    try {
      var postalCodeResponse = postalCodeService.fetchPostalCode(orgId, postalCode);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Zip Code Details successfully fetched")
              .payload(postalCodeResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get zip code request", e);
      throw e;
    }
  }

  /**
   * Deletes a zip code for the given organization ID and zip code.
   *
   * <p>This method processes a DELETE request to remove a zip code identified by the given
   * organization ID and zip code.
   *
   * @param orgId The unique identifier of the organization.
   * @param postalCode The zip code of the source or destination node.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     deleted zip code.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   */
  @Operation(
      summary = "Delete Zip Code",
      description = "Deletes the zip code for the given organization ID and zip code.")
  @DeletePostalCodeDetails
  @DeleteMapping("/{orgId}/{postalCode}")
  @Transactional
  public ResponseEntity<BaseResponse<PostalCodeResponse>> deletePostalCode(
      @NotNull
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull
          @PathVariable
          @Parameter(
              description = "Zip code of the source or destination node.",
              example = "T2PS2K")
          String postalCode)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing delete zip code request");
    try {
      var postalCodeResponse = postalCodeService.processRemovePostalCode(orgId, postalCode);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Zip Code Details successfully deleted")
              .payload(postalCodeResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process delete zip code request", e);
      throw e;
    }
  }

  /**
   * Retrieves a list of zip codes by the organization ID and zip code prefix.
   *
   * <p>This method processes a GET request to fetch a list of zip codes based on the organization
   * ID and the provided zip code prefix.
   *
   * @param orgId The unique identifier of the organization.
   * @param zipCodePrefix The first three characters of the zip code.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of zip codes.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   */
  @Operation(
      summary = "Get Zip Code List",
      description =
          "Retrieves the list of the zip codes by the organization ID and zip code prefix.")
  @GetPostalCodeListByPrefix
  @GetMapping("/{orgId}")
  public ResponseEntity<BaseResponse<List<PostalCodeResponse>>> getByPostalCodePrefix(
      @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @RequestParam
          @Parameter(description = "First three characters of the zip code.", example = "T2P")
          String zipCodePrefix)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing get zip code list by zip code prefix request");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("zip Code Details List successfully fetched")
            .payload(postalCodeService.fetchByPostalCodePrefix(orgId, zipCodePrefix))
            .build());
  }

  /**
   * Retrieves the custom region ID by the organization ID and zip code.
   *
   * <p>This method processes a GET request to fetch the custom region ID for the given organization
   * ID and zip code.
   *
   * @param orgId The unique identifier of the organization.
   * @param postalCode The zip code of the source or destination node.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the custom region ID
   *     details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs while processing the
   *     request.
   */
  @Operation(
      summary = "Get Custom Region ID",
      description = "Retrieves the custom region ID  by the zip code.")
  @GetCustomRegionIdDetails
  @GetMapping("/orgId/{orgId}/postal-code/{postalCode}")
  public ResponseEntity<BaseResponse<CustomRegionResponse>> fetchCustomRegionIdByPostalCode(
      @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @PathVariable
          String orgId,
      @Parameter(description = "Zip code of the source or destination node.", example = "T2PS2K")
          @PathVariable
          String postalCode)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("--Processing get custom regions id request --");

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Custom Regions Id details fetched successfully")
            .payload(postalCodeService.fetchCustomRegionIdByPostalCode(orgId, postalCode))
            .build());
  }

  /**
   * Retrieves the timezone for zip codes by the organization ID and country.
   *
   * <p>This method processes a GET request to fetch the timezone details for zip codes based on the
   * organization ID and country.
   *
   * @param orgId The unique identifier of the organization.
   * @param country The country of the source or destination node.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the timezone details
   *     for zip codes.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   */
  @GetPostalCodeTimezoneForOrgIdAndCountryDetails
  @GetMapping("/market-region/org/{orgId}")
  public ResponseEntity<BaseResponse<List<PostalCodeResponse>>>
      getPostalCodeTimeZoneForOrgIdAndCountry(
          @NotBlank(message = "orgId can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              String orgId,
          @RequestParam
              @Parameter(description = "Country of the source or destination node.", example = "US")
              String country)
          throws PromiseEngineException {
    logger.debug("Processing get zip code prefix list for orgId and state");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Zip Code Timezone successfully fetched!")
            .payload(postalCodeService.fetchPostalCodeTimezoneByOrgIdAndCountry(orgId, country))
            .build());
  }

  /**
   * Retrieves market regions for a given organization ID.
   *
   * <p>This method processes a GET request to fetch the market regions associated with the given
   * organization ID.
   *
   * @param orgId The unique identifier of the organization.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the market regions for
   *     the organization.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   */
  @GetMargetRegionsForOrgIdDetails
  @GetMapping("/market-regions/org/{orgId}")
  public ResponseEntity<BaseResponse<List<MarketRegionInfo>>> getMarketRegionsForOrgId(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId)
      throws PromiseEngineException {
    logger.debug("Processing get market regions for orgId");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Market Regions are fetched successfully")
            .payload(postalCodeService.getMarketRegionForOrgId(orgId))
            .build());
  }

  /**
   * Retrieves zip code prefixes for a given organization ID and state.
   *
   * <p>This method processes a GET request to fetch zip code prefixes for the provided organization
   * ID and state.
   *
   * @param orgId The unique identifier of the organization.
   * @param state The state of the source or destination node.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of zip code
   *     prefixes.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   */
  @GetPostalCodePrefixListForOrgIdAndStateDetails
  @GetMapping("/list/org/{orgId}")
  public ResponseEntity<BaseResponse<List<String>>> getPostalCodePrefixForOrgIdAndState(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @RequestParam
          @Parameter(description = "State of the source or destination node.", example = "NY")
          String state)
      throws PromiseEngineException {
    logger.debug("Processing get zip code prefix list for orgId and state");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("")
            .payload(postalCodeService.fetchPostalCodePrefixForOrgIdAndState(orgId, state))
            .build());
  }

  /**
   * Retrieves the list of state and zip code prefixes for a given organization ID.
   *
   * <p>This method processes a GET request to fetch the list of state and zip code prefixes based
   * on the organization ID.
   *
   * @param orgId The unique identifier of the organization.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the state and zip code
   *     prefix list.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   */
  @GetPostalCodePrefixListDetails
  @GetMapping("/ui/state-postal-code-prefix/orgId/{orgId}")
  public ResponseEntity<BaseResponse<List<PostalCodePrefixDto>>> fetchPostalCodePrefixList(
      @NotBlank(message = "orgId can't be empty")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId)
      throws PromiseEngineException {
    logger.debug("Processing get state and zip code prefixes list");

    List<PostalCodePrefixDto> responseList = postalCodeService.getPostalCodePrefixList(orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("State and Zip Code Prefix list fetched successfully")
            .payload(responseList)
            .build());
  }
}
