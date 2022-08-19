package com.hbc.carrier.controller;

import com.hbc.carrier.domain.inbound.CarrierServiceRequest;
import com.hbc.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.carrier.domain.pojo.PageParams;
import com.hbc.carrier.exception.CarrierServiceDomainException;
import com.hbc.carrier.service.CarrierServiceService;
import com.hbc.common.base.PagePayload;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.util.PaginationUtil;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carrier-service")
@RequiredArgsConstructor
public class CarrierServiceController {

  private static final Logger logger = LoggerFactory.getLogger(CarrierServiceController.class);
  private final CarrierServiceService carrierserviceService;

  @Value("${pagination.pageNo}")
  private Integer defaultPageNo;

  @Value("${pagination.pageSize}")
  private Integer defaultPageSize;

  @Value("${pagination.sortBy}")
  private String defaultSortBy;

  @Value("${pagination.sortOrder}")
  private String defaultSortOrder;

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
    logger.debug("Processing get carrier service list by orgId");
    Page<CarrierServiceResponse> carrierServiceResponses =
        carrierserviceService.getCarrierServiceList(
            orgId,
            pageParams.getPageNo().orElse(defaultPageNo),
            pageParams.getPageSize().orElse(defaultPageSize),
            pageParams.getSortBy().orElse(defaultSortBy),
            pageParams.getSortOrder().orElse(defaultSortOrder));

    PagePayload<CarrierServiceResponse> pagePayload =
        setCarrierServicePagePayload(carrierServiceResponses, pageParams);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("CarrierService list fetched successfully")
            .payload(pagePayload)
            .build());
  }

  private PagePayload<CarrierServiceResponse> setCarrierServicePagePayload(
      Page<CarrierServiceResponse> carrierServiceResponses, PageParams pageParams) {
    PagePayload<CarrierServiceResponse> pagePayload = new PagePayload<>();
    var pagination = new PagePayload.Pagination();
    pagination.setTotalRecords((int) carrierServiceResponses.getTotalElements());
    pagination.setTotalPages(carrierServiceResponses.getTotalPages());
    pagination.setCurrentPage(pageParams.getPageNo().orElse(defaultPageNo));
    pagination.setSortOrder(pageParams.getSortOrder().orElse(defaultSortOrder));
    pagination.setSortBy(pageParams.getSortBy().orElse(defaultSortBy));

    String nextUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(defaultPageNo),
            carrierServiceResponses.getTotalPages(),
            "next",
            String.format(
                "/{orgId}?pageNo=%d&pageSize=%d",
                (pageParams.getPageNo().orElse(defaultPageNo) + 1),
                pageParams.getPageSize().orElse(defaultPageSize)));
    String previousUri =
        PaginationUtil.buildUriForPagination(
            pageParams.getPageNo().orElse(defaultPageNo),
            carrierServiceResponses.getTotalPages(),
            "previous",
            String.format(
                "/{orgId}?pageNo=%d&pageSize=%d",
                (pageParams.getPageNo().orElse(defaultPageNo) - 1),
                pageParams.getPageSize().orElse(defaultPageSize)));
    pagination.setNext(nextUri);
    pagination.setPrevious(previousUri);
    pagePayload.setPagination(pagination);
    pagePayload.setData(carrierServiceResponses.getContent());

    return pagePayload;
  }
}
