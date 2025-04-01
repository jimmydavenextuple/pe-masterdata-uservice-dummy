package com.nextuple.node.persistence.service.impl;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.node.persistence.domain.VendorDomainDto;
import com.nextuple.node.persistence.domain.key.VendorDomainKey;
import com.nextuple.node.persistence.entity.VendorEntity;
import com.nextuple.node.persistence.entity.key.VendorKey;
import com.nextuple.node.persistence.exception.VendorDomainException;
import com.nextuple.node.persistence.mapper.VendorEntityMapper;
import com.nextuple.node.persistence.repository.VendorRepository;
import com.nextuple.node.persistence.service.VendorPersistenceService;
import com.nextuple.postgres.service.CommonPersistenceService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VendorPersistenceServiceImpl
    extends CommonPersistenceService<
        VendorDomainDto,
        VendorDomainKey,
        VendorEntity,
        VendorKey,
        VendorRepository,
        VendorEntityMapper>
    implements VendorPersistenceService {

  private static final Logger logger = LoggerFactory.getLogger(VendorPersistenceServiceImpl.class);

  @Override
  public VendorDomainDto saveVendorDetails(VendorDomainDto vendorDomainDto)
      throws VendorDomainException {
    try {
      return save(vendorDomainDto);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to save vendor");
      throw new VendorDomainException(
          "Error while saving the vendor",
          vendorDomainDto.getVendorId(),
          vendorDomainDto.getOrgId());
    }
  }

  @Override
  public Optional<VendorDomainDto> findVendorByVendorIdAndOrgId(String vendorId, String orgId)
      throws VendorDomainException {
    try {
      return findByKey(VendorDomainKey.builder().orgId(orgId).vendorId(vendorId).build());
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find vendor");
      throw new VendorDomainException("Error while finding vendor", vendorId, orgId);
    }
  }

  @Override
  public void deleteVendor(VendorDomainDto vendorDomainDto) throws VendorDomainException {
    try {
      delete(vendorDomainDto);
      // todo : add removal of item vendor mapping
      // todo : add removal of nodes associated with this vendor.
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete node");
      throw new VendorDomainException(
          "Error while deleting vendor", vendorDomainDto.getVendorId(), vendorDomainDto.getOrgId());
    }
  }
}
