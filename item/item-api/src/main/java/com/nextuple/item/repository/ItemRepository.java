package com.nextuple.item.repository;

import com.nextuple.item.domain.entity.ItemEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, String> {
  Optional<ItemEntity> findByItemIdAndOrgIdAndUom(String itemId, String orgId, String uom);

  List<ItemEntity> findByOrgIdAndItemIdIn(String orgId, List<String> itemList);
}
