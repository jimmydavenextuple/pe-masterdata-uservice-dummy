package com.hbc.carrier.controller;

import static com.hbc.common.constants.CommonConstants.CARRIER_DEFAULT_SORT_BY;
import static com.hbc.common.constants.CommonConstants.DEFAULT_SORT_ORDER;

import com.hbc.carrier.domain.dto.CarrierCacheKeyDto;
import com.hbc.carrier.domain.inbound.CarrierServiceRequest;
import com.hbc.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.carrier.exception.CarrierServiceDomainException;
import com.hbc.carrier.service.CarrierServiceService;
import com.hbc.common.base.PagePayload;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.pojo.PageParams;
import com.hbc.common.pojo.PageProperties;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.util.PaginationUtil;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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
@RequestMapping("/carrier-service")
@RequiredArgsConstructor
public class CarrierServiceController {

  private static final Logger logger = LoggerFactory.getLogger(CarrierServiceController.class);
  private static final String PAGINATION_URL = "/%s?pageNo=%d&pageSize=%d";
  private final CarrierServiceService carrierserviceService;
  private final PageProperties pageProperties;

  @PostMapping
  public ResponseEntity<BaseResponse<CarrierServiceResponse>> createCarrierService(
      @Valid @RequestBody CarrierServiceRequest carrierServiceRequest)
      throws CarrierServiceDomainException {
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

  @GetMapping("/{carrierId}/{carrierServiceId}/{orgId}")
  public ResponseEntity<BaseResponse<CarrierServiceResponse>> getCarrierServiceDetails(
      @NotBlank @PathVariable String carrierId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String orgId)
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

  @PutMapping("/{carrierId}/{carrierServiceId}/{orgId}")
  public ResponseEntity<BaseResponse<CarrierServiceResponse>> updateCarrierServiceDetails(
      @NotBlank @PathVariable String carrierId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String orgId,
      @Valid @RequestBody CarrierServiceUpdateRequest carrierServiceUpdateRequest)
      throws CarrierServiceDomainException, CommonServiceException {
    logger.debug("Processing update CarrierService details");
    try {

      var carrierServiceResponse =
          carrierserviceService.updateCarrierServiceDetails(
              carrierId, carrierServiceId, orgId, carrierServiceUpdateRequest);
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

  @DeleteMapping("/{carrierId}/{carrierServiceId}/{orgId}")
  public ResponseEntity<BaseResponse<CarrierServiceResponse>> deleteCarrierService(
      @NotBlank @PathVariable String carrierId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String orgId)
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

  @GetMapping("/{orgId}")
  public ResponseEntity<BaseResponse<PagePayload<CarrierServiceResponse>>>
      getCarrierServiceListWithPagination(@PathVariable String orgId, PageParams pageParams)
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

  @GetMapping("/{carrierServiceId}/{orgId}")
  public ResponseEntity<BaseResponse<List<CarrierServiceResponse>>>
      getCarrierServiceDetailsByCarrierServiceIdAndOrgId(
          @NotBlank @PathVariable String carrierServiceId, @NotBlank @PathVariable String orgId)
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

  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<CarrierCacheKeyDto>>> getCarrierCacheKeys(
      @RequestParam Integer limit) throws CarrierServiceDomainException {
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
            String.format(
                PAGINATION_URL,
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) + 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));
    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(pageProperties.getPageNo()),
            carrierServiceResponses.getTotalPages(),
            "previous",
            String.format(
                PAGINATION_URL,
                orgId,
                (pageParams.getPageNo().orElse(pageProperties.getPageNo()) - 1),
                pageParams.getPageSize().orElse(pageProperties.getPageSize())));
    pagination.setNext(nextUri);
    pagination.setPrevious(previousUri);
    pagePayload.setPagination(pagination);
    pagePayload.setData(carrierServiceResponses.getContent());

    return pagePayload;
  }

  @GetMapping("/orgId/{orgId}")
  public ResponseEntity<BaseResponse<List<CarrierServiceResponse>>>
  getCarrierServiceListByOrgId(@PathVariable String orgId)
          throws CarrierServiceDomainException {
    logger.debug("Processing get carrier service list by orgId");
    List<CarrierServiceResponse> carrierServiceResponses = carrierserviceService.getCarrierServiceListByOrgId(orgId);
    return ResponseEntity.ok(
            BaseResponse.builder()
                    .message("CarrierService list fetched successfully")
                    .payload(carrierServiceResponses)
                    .build());
  }
}
