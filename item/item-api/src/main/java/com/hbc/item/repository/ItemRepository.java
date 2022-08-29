package com.hbc.item.repository;

import com.hbc.item.domain.entity.ItemEntity;
import com.hbc.postgres.config.ReaderDS;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, String> {
  @ReaderDS
  Optional<ItemEntity> findByItemIdAndOrgIdAndUom(String itemId, String orgId, String uom);
}
