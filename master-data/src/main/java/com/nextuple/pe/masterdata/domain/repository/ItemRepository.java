package com.nextuple.pe.masterdata.domain.repository;

import com.nextuple.pe.masterdata.domain.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, String> {

  Optional<ItemEntity> findByItemIdAndOrgIdAndUom(String itemId, String orgId, String uom);
}
