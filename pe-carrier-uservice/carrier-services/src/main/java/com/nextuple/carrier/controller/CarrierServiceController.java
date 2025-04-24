/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.carrier.controller;

import static com.nextuple.common.constants.CommonConstants.CARRIER_DEFAULT_SORT_BY;
import static com.nextuple.common.constants.CommonConstants.DEFAULT_SORT_ORDER;

import com.nextuple.carrier.controller.docs.CreateCarrierServiceDoc;
import com.nextuple.carrier.controller.docs.DeleteCarrierServiceDoc;
import com.nextuple.carrier.controller.docs.GetCarrierCacheKeysDoc;
import com.nextuple.carrier.controller.docs.GetCarrierServiceDetailsByCarrierServiceIdAndOrgId;
import com.nextuple.carrier.controller.docs.GetCarrierServiceDetailsDoc;
import com.nextuple.carrier.controller.docs.GetCarrierServiceListByOrgIdDoc;
import com.nextuple.carrier.controller.docs.GetCarrierServiceListWithPaginationDoc;
import com.nextuple.carrier.controller.docs.UpdateCarrierServiceDetailsDoc;
import com.nextuple.carrier.controller.docs.UpsertCarrierServiceDoc;
import com.nextuple.carrier.domain.dto.CarrierCacheKeyDto;
import com.nextuple.carrier.domain.inbound.CarrierServiceBaseRequest;
import com.nextuple.carrier.domain.inbound.CarrierServiceRequest;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.carrier.persistence.exception.CarrierServiceDomainException;
import com.nextuple.carrier.service.CarrierServiceService;
import com.nextuple.common.base.PagePayload;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.pojo.PageProperties;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.util.PaginationUtil;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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
 * Controller for managing carrier services.
 *
 * <p>This controller provides APIs to create, update, delete, and retrieve carrier services. It
 * also supports fetching carrier service details by carrier service ID and organization ID, as well
 * as retrieving the list of carrier services for a specific organization with pagination.
 *
 * <p>The controller is tagged with "Carrier Service APIs" for easy categorization in API
 * documentation.
 */
@Validated
@RestController
@Tag(name = "Carrier Service APIs")
@RequestMapping("/carrier-service")
@RequiredArgsConstructor
public class CarrierServiceController {

  private static final Logger logger = LoggerFactory.getLogger(CarrierServiceController.class);
  private static final String PAGINATION_URL = "/%s?pageNo=%d&pageSize=%d";
  private final CarrierServiceService carrierserviceService;
  private final PageProperties pageProperties;

  /**
   * Creates or updates a carrier service.
   *
   * <p>This method processes a POST request to upsert (create or update) a carrier service based on
   * the given request data. If the carrier service already exists, it will be updated; otherwise, a
   * new one will be created.
   *
   * @param carrierServiceRequest The request body containing the carrier service details.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the created or updated
   *     carrier service.
   * @throws CarrierServiceDomainException If a domain-specific error occurs.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @PostMapping("/upsert")
  @UpsertCarrierServiceDoc
  public ResponseEntity<BaseResponse<CarrierServiceResponse>> upsertCarrierService(
      @Valid @RequestBody CarrierServiceRequest carrierServiceRequest)
      throws CarrierServiceDomainException, CommonServiceException {

    logger.debug("Processing upsert carrier service request");
    try {
      CarrierServiceResponse carrierServiceResponse =
          carrierserviceService.upsertCarrierService(carrierServiceRequest);

      logger.info("CarrierService upserted successfully: {}", carrierServiceResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Carrier Service saved successfully")
              .payload(carrierServiceResponse)
              .build());

    } catch (Exception e) {
      logger.error("Failed to upsert CarrierService");
      throw e;
    }
  }

  /**
   * Creates a new carrier service.
   *
   * <p>This method processes a POST request to create a carrier service based on the details
   * provided in the request body.
   *
   * @param carrierServiceRequest The request body containing the details for the carrier service to
   *     be created.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the created carrier
   *     service details.
   * @throws CarrierServiceDomainException If a domain-specific error occurs.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @PostMapping
  @CreateCarrierServiceDoc
  public ResponseEntity<BaseResponse<CarrierServiceResponse>> createCarrierService(
      @Valid @RequestBody CarrierServiceRequest carrierServiceRequest)
      throws CarrierServiceDomainException, CommonServiceException {
    logger.debug("Processing CarrierService creation request");
    try {
      var carrierServiceResponse =
          carrierserviceService.createCarrierService(carrierServiceRequest);
      logger.info("Response after creation of carrier :{}", carrierServiceResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Carrier Service successfully created")
              .payload(carrierServiceResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to create CarrierService");
      throw e;
    }
  }

  /**
   * Retrieves the details of a specific carrier service.
   *
   * <p>This method processes a GET request to fetch the details of a carrier service identified by
   * the given carrier ID, carrier service ID, and organization ID.
   *
   * @param carrierId The unique identifier of the carrier.
   * @param carrierServiceId The unique identifier of the carrier service.
   * @param orgId The unique identifier of the organization.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the fetched carrier
   *     service details.
   * @throws CarrierServiceDomainException If a domain-specific error occurs.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @GetCarrierServiceDetailsDoc
  @GetMapping("/{carrierId}/{carrierServiceId}/{orgId}")
  public ResponseEntity<BaseResponse<CarrierServiceResponse>> getCarrierServiceDetails(
      @Parameter(description = "Unique identifier of the carrier.", example = "UPS")
          @NotBlank(message = "carrierId can't be empty")
          @PathVariable
          String carrierId,
      @Parameter(description = "Unique identifier of the carrier service.", example = "UPS-GROUND")
          @NotBlank(message = "carrierServiceId can't be empty")
          @PathVariable
          String carrierServiceId,
      @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @NotBlank(message = "orgId can't be empty")
          @PathVariable
          String orgId)
      throws CarrierServiceDomainException, CommonServiceException {
    logger.debug("Processing get CarrierService details");
    try {

      var carrierServiceResponse =
          carrierserviceService.getCarrierServiceDetails(carrierId, carrierServiceId, orgId);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("CarrierService details fetched successfully")
              .payload(carrierServiceResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch CarrierService details");
      throw e;
    }
  }

  /**
   * Updates an existing carrier service.
   *
   * <p>This method processes a PUT request to update a carrier service based on the given carrier
   * ID, carrier service ID, organization ID, and details provided in the request body.
   *
   * @param carrierId The unique identifier of the carrier.
   * @param carrierServiceId The unique identifier of the carrier service.
   * @param orgId The unique identifier of the organization.
   * @param carrierServiceBaseRequest The request body containing the updated details for the
   *     carrier service.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the updated carrier
   *     service details.
   * @throws CarrierServiceDomainException If a domain-specific error occurs.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @UpdateCarrierServiceDetailsDoc
  @PutMapping("/{carrierId}/{carrierServiceId}/{orgId}")
  public ResponseEntity<BaseResponse<CarrierServiceResponse>> updateCarrierServiceDetails(
      @Parameter(description = "Unique identifier of the carrier.", example = "UPS")
          @NotBlank(message = "carrierId can't be empty")
          @PathVariable
          String carrierId,
      @Parameter(description = "Unique identifier of the carrier service.", example = "UPS-GROUND")
          @NotBlank(message = "carrierServiceId can't be empty")
          @PathVariable
          String carrierServiceId,
      @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @NotBlank(message = "orgId can't be empty")
          @PathVariable
          String orgId,
      @Valid @RequestBody CarrierServiceBaseRequest carrierServiceBaseRequest)
      throws CarrierServiceDomainException, CommonServiceException {
    logger.debug("Processing update CarrierService details");
    try {

      var carrierServiceResponse =
          carrierserviceService.updateCarrierServiceDetails(
              carrierId, carrierServiceId, orgId, carrierServiceBaseRequest);
      logger.info("Response after updation of carrier :{}", carrierServiceResponse);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("CarrierService details updated successfully")
              .payload(carrierServiceResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to update CarrierService details");
      throw e;
    }
  }

  /**
   * Deletes a carrier service.
   *
   * <p>This method processes a DELETE request to remove a carrier service identified by the given
   * carrier ID, carrier service ID, and organization ID.
   *
   * @param carrierId The unique identifier of the carrier.
   * @param carrierServiceId The unique identifier of the carrier service.
   * @param orgId The unique identifier of the organization.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the details of the
   *     deleted carrier service.
   * @throws CarrierServiceDomainException If a domain-specific error occurs.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @DeleteCarrierServiceDoc
  @DeleteMapping("/{carrierId}/{carrierServiceId}/{orgId}")
  public ResponseEntity<BaseResponse<CarrierServiceResponse>> deleteCarrierService(
      @Parameter(description = "Unique identifier of the carrier.", example = "UPS")
          @NotBlank(message = "carrierId can't be empty")
          @PathVariable
          String carrierId,
      @Parameter(description = "Unique identifier of the carrier service.", example = "UPS-GROUND")
          @NotBlank(message = "carrierServiceId can't be empty")
          @PathVariable
          String carrierServiceId,
      @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @NotBlank(message = "orgId can't be empty")
          @PathVariable
          String orgId)
      throws CarrierServiceDomainException, CommonServiceException {
    logger.debug("Processing delete CarrierService");
    try {
      var carrierServiceResponse =
          carrierserviceService.deleteCarrierService(carrierId, carrierServiceId, orgId);
      logger.info("Response after deletion of carrier :{}", carrierServiceResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("CarrierService deleted successfully")
              .payload(carrierServiceResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to ");
      throw e;
    }
  }

  /**
   * Retrieves a paginated list of carrier services for a specific organization.
   *
   * <p>This method processes a GET request to fetch a list of carrier services for a given
   * organization ID, with pagination applied.
   *
   * @param orgId The unique identifier of the organization.
   * @param pageParams The pagination parameters, including page number, page size, and sorting
   *     details.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the paginated list of
   *     carrier services.
   * @throws CarrierServiceDomainException If a domain-specific error occurs.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @GetCarrierServiceListWithPaginationDoc
  @GetMapping("/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<CarrierServiceResponse>>>
      getCarrierServiceListWithPagination(
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
              @NotBlank(message = "orgId can't be empty")
              @PathVariable
              String orgId,
          PageParams pageParams)
          throws CarrierServiceDomainException, CommonServiceException {
    logger.debug("Processing get carrier service list by orgId with pagination");
    Page<CarrierServiceResponse> carrierServiceResponses =
        carrierserviceService.getCarrierServiceList(
            orgId,
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            pageParams.getPageSize().orElse(pageProperties.getPageSize()),
            pageParams.getSortBy().orElse(CARRIER_DEFAULT_SORT_BY),
            pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));

    PagePayload<CarrierServiceResponse> pagePayload =
        setCarrierServicePagePayload(carrierServiceResponses, pageParams, orgId);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("CarrierService list fetched successfully")
            .payload(pagePayload)
            .build());
  }

  /**
   * Retrieves the details of carrier services by carrier service ID and organization ID.
   *
   * <p>This method processes a GET request to fetch the details of carrier services associated with
   * the given carrier service ID and organization ID.
   *
   * @param carrierServiceId The unique identifier of the carrier service.
   * @param orgId The unique identifier of the organization.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of carrier
   *     service details.
   * @throws CarrierServiceDomainException If a domain-specific error occurs.
   * @throws CommonServiceException If a general service-related error occurs.
   */
  @GetCarrierServiceDetailsByCarrierServiceIdAndOrgId
  @GetMapping("/{carrierServiceId}/{orgId}")
  public ResponseEntity<BaseResponse<List<CarrierServiceResponse>>>
      getCarrierServiceDetailsByCarrierServiceIdAndOrgId(
          @Parameter(
                  description = "Unique identifier of the carrier service.",
                  example = "UPS-GROUND")
              @NotBlank(message = "carrierServiceId can't be empty")
              @PathVariable
              String carrierServiceId,
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
              @NotBlank(message = "orgId can't be empty")
              @PathVariable
              String orgId)
          throws CarrierServiceDomainException, CommonServiceException {
    logger.debug("Processing get CarrierService details");
    try {

      var carrierServiceResponse =
          carrierserviceService.getCarrierServiceDetailsByCarrierIdAndOrgId(
              carrierServiceId, orgId);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("CarrierService details fetched successfully")
              .payload(carrierServiceResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch CarrierService details");
      throw e;
    }
  }

  /**
   * Retrieves all carrier cache keys up to a specified limit.
   *
   * <p>This method processes a GET request to fetch carrier cache keys.
   *
   * @param limit The maximum number of cache keys to retrieve.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of carrier
   *     cache keys.
   * @throws CarrierServiceDomainException If a domain-specific error occurs.
   */
  @GetCarrierCacheKeysDoc
  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<CarrierCacheKeyDto>>> getCarrierCacheKeys(
      @Parameter(description = "Limit of the cache key.") @RequestParam Integer limit)
      throws CarrierServiceDomainException {
    logger.debug("Processing get Carrier Cache Keys");

    var response = carrierserviceService.getAllCarrierCacheKeys(limit);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Carrier Cache Keys fetched successfully")
            .payload(response)
            .build());
  }

  private PagePayload<CarrierServiceResponse> setCarrierServicePagePayload(
      Page<CarrierServiceResponse> carrierServiceResponses, PageParams pageParams, String orgId) {
    PagePayload<CarrierServiceResponse> pagePayload = new PagePayload<>();
    var pagination = new PagePayload.Pagination();
    pagination.setTotalRecords((int) carrierServiceResponses.getTotalElements());
    pagination.setTotalPages(carrierServiceResponses.getTotalPages());
    pagination.setCurrentPage(pageParams.getPageNo().orElse(pageProperties.getPageNo()));
    pagination.setSortOrder(pageParams.getSortOrder().orElse(DEFAULT_SORT_ORDER));
    pagination.setSortBy(pageParams.getSortBy().orElse(CARRIER_DEFAULT_SORT_BY));

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            carrierServiceResponses.getTotalPages(),
            "next",
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));
    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            carrierServiceResponses.getTotalPages(),
            "previous",
            PAGINATION_URL.formatted(
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));
    pagination.setNext(nextUri);
    pagination.setPrevious(previousUri);
    pagePayload.setPagination(pagination);
    pagePayload.setData(carrierServiceResponses.getContent());

    return pagePayload;
  }

  /**
   * Retrieves the list of carrier services for a specific organization.
   *
   * <p>This method processes a GET request to fetch a list of carrier services for the given
   * organization ID.
   *
   * @param orgId The unique identifier of the organization.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of carrier
   *     services.
   * @throws CarrierServiceDomainException If a domain-specific error occurs.
   */
  @GetCarrierServiceListByOrgIdDoc
  @GetMapping("/orgId/{orgId}")
  public ResponseEntity<BaseResponse<List<CarrierServiceResponse>>> getCarrierServiceListByOrgId(
      @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @NotBlank(message = "orgId can't be empty")
          @PathVariable
          String orgId)
      throws CarrierServiceDomainException {
    logger.debug("Processing get carrier service list by orgId");
    List<CarrierServiceResponse> carrierServiceResponses =
        carrierserviceService.getCarrierServiceListByOrgId(orgId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("CarrierService list fetched successfully")
            .payload(carrierServiceResponses)
            .build());
  }
}
