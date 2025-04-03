package com.nextuple.vendor.persistence.repository;

import com.nextuple.postgres.repository.CommonJpaRepository;
import com.nextuple.vendor.persistence.entity.VendorEntity;
import com.nextuple.vendor.persistence.entity.key.VendorKey;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends CommonJpaRepository<VendorEntity, VendorKey> {}
