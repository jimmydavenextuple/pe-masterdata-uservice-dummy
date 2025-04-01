package com.nextuple.node.persistence.repository;

import com.nextuple.node.persistence.entity.VendorEntity;
import com.nextuple.node.persistence.entity.key.VendorKey;
import com.nextuple.postgres.repository.CommonJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends CommonJpaRepository<VendorEntity, VendorKey> {}
