package com.nextuple.service.inventory.repository;

import com.nextuple.service.inventory.domain.entity.ServiceOptionInventoryTypeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceInventoryRepository
    extends JpaRepository<ServiceOptionInventoryTypeEntity, String> {
  Optional<ServiceOptionInventoryTypeEntity>
      findServiceOptionInventoryTypeEntityByOrgIdAndServiceOption(
          String orgId, String serviceOption);
}
