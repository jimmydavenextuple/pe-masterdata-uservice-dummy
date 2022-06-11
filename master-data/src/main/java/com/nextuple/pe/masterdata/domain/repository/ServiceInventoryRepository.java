package com.nextuple.pe.masterdata.domain.repository;

import com.nextuple.pe.masterdata.domain.entity.ServiceOptionInventoryTypeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceInventoryRepository
    extends JpaRepository<ServiceOptionInventoryTypeEntity, String> {
  Optional<ServiceOptionInventoryTypeEntity>
      findServiceOptionInventoryTypeEntityByOrgIdAndServiceOption(
          String orgId, String serviceOption);
}
