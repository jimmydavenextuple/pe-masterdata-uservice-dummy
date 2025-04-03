package com.nextuple.vendor.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.postgres.config.ReaderDS;
import com.nextuple.vendor.domain.inbound.VendorRequest;
import com.nextuple.vendor.domain.inbound.VendorUpdationRequest;
import com.nextuple.vendor.domain.mapper.VendorMapper;
import com.nextuple.vendor.domain.outbound.VendorResponse;
import com.nextuple.vendor.persistence.domain.VendorDomainDto;
import com.nextuple.vendor.persistence.exception.VendorDomainException;
import com.nextuple.vendor.persistence.service.VendorPersistenceService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VendorService {
  private static final Logger logger = LoggerFactory.getLogger(VendorService.class);
  public static final VendorMapper INSTANCE = Mappers.getMapper(VendorMapper.class);
  private final VendorPersistenceService vendorPersistenceService;
  private static final String ORG_ID = "orgId";
  private static final String VENDOR_ID = "vendorId";
  private static final String VENDOR_EXCEPTION_MESSAGE = "Vendor not found with given details";

  public VendorResponse createVendor(VendorRequest vendorRequest) throws VendorDomainException {
    var vendorDetails = INSTANCE.vendorRequestToVendorEntity(vendorRequest);
    return INSTANCE.toVendorResponse(vendorPersistenceService.saveVendorDetails(vendorDetails));
  }

  public VendorResponse updateVendorDetails(
      String vendorId, String orgId, VendorUpdationRequest vendorUpdationRequest)
      throws VendorDomainException, CommonServiceException {
    Optional<VendorDomainDto> existingVendorDetails =
        vendorPersistenceService.findVendorByVendorIdAndOrgId(vendorId, orgId);
    if (existingVendorDetails.isEmpty()) {
      logger.error(VENDOR_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(VENDOR_ID, FieldError.builder().rejectedValue(vendorId).build());
      throw new CommonServiceException(
          VENDOR_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    logger.info("Response before updation of vendor :{}", existingVendorDetails.get());
    INSTANCE.updateVendorDetails(vendorUpdationRequest, existingVendorDetails.get());
    return INSTANCE.toVendorResponse(
        vendorPersistenceService.saveVendorDetails(existingVendorDetails.get()));
  }

  @ReaderDS
  public VendorResponse getVendorDetails(String vendorId, String orgId)
      throws VendorDomainException, CommonServiceException {
    Optional<VendorDomainDto> vendorDetails =
        vendorPersistenceService.findVendorByVendorIdAndOrgId(vendorId, orgId);
    if (vendorDetails.isEmpty()) {
      logger.error(VENDOR_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(VENDOR_ID, FieldError.builder().rejectedValue(vendorId).build());
      throw new CommonServiceException(
          VENDOR_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    return INSTANCE.toVendorResponse(vendorDetails.get());
  }

  public VendorResponse deleteVendor(String vendorId, String orgId)
      throws VendorDomainException, CommonServiceException {
    Optional<VendorDomainDto> vendorDetails =
        vendorPersistenceService.findVendorByVendorIdAndOrgId(vendorId, orgId);
    if (vendorDetails.isEmpty()) {
      logger.error(VENDOR_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(VENDOR_ID, FieldError.builder().rejectedValue(vendorId).build());
      throw new CommonServiceException(
          VENDOR_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }
    logger.info(
        "Response before deletion of vendor :{}", INSTANCE.toVendorResponse(vendorDetails.get()));
    var vendorResponse = INSTANCE.toVendorResponse(vendorDetails.get());
    vendorPersistenceService.deleteVendor(vendorDetails.get());
    return vendorResponse;
  }
}
