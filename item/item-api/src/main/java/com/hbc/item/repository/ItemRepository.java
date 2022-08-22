package com.hbc.item.repository;

import com.hbc.item.domain.entity.ItemEntity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, String> {

  Optional<ItemEntity> findByItemIdAndOrgIdAndUom(String itemId, String orgId, String uom);


  @Query(
          value =
                  "SELECT * FROM item i WHERE i.org_id = ?1 AND i.uom = ?2  AND i.item_id IN ?3 ",
          nativeQuery = true)
  List<ItemEntity> findItemListByItemIdAndOrgIdAndUom(String orgId, String uom, List<String> itemList);
}
