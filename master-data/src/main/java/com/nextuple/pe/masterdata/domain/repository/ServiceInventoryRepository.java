package com.nextuple.pe.masterdata.domain.repository;

import com.nextuple.pe.masterdata.domain.entity.ServiceOptionInventoryTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceInventoryRepository
    extends JpaRepository<ServiceOptionInventoryTypeEntity, String> {
  Optional<ServiceOptionInventoryTypeEntity>
      findServiceOptionInventoryTypeEntityByOrgIdAndServiceOption(
          String orgId, String serviceOption);
}
