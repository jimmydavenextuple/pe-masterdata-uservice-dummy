/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.persistence.repository;

import com.nextuple.item.persistence.entity.ItemBufferEntity;
import com.nextuple.item.persistence.entity.key.ItemBufferKey;
import com.nextuple.postgres.repository.CommonJpaRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemBufferRepository extends CommonJpaRepository<ItemBufferEntity, ItemBufferKey> {

  List<ItemBufferEntity> findByItemIdAndOrgIdAndUom(String itemId, String orgId, String uom);

  Optional<ItemBufferEntity>
      findByItemIdAndOrgIdAndUomAndAndBufferStartDateAndBufferEndDateAndBufferHours(
          String itemId,
          String orgId,
          String uom,
          Date bufferStartDate,
          Date bufferEndDate,
          Double bufferHours);

  Optional<ItemBufferEntity> findByItemIdAndOrgIdAndUomAndBufferStartDateAndBufferEndDate(
      String itemId, String orgId, String uom, Date bufferStartDate, Date bufferEndDate);

  Optional<ItemBufferEntity> findByOrgIdAndId(String orgId, Long id);

  @Query(
      value = "select distinct on (item_id, org_id, uom) * From item_buffers_data where org_id= ?1",
      countQuery =
          "SELECT COUNT(*) FROM (SELECT DISTINCT ON (item_id, org_id, uom) item_id, org_id, uom FROM item_buffers_data WHERE org_id = ?1) AS subquery",
      nativeQuery = true)
  Page<ItemBufferEntity> findDistinctItemIdAndOrgIdAndUomByOrgId(String orgId, Pageable pageable);

  List<ItemBufferEntity> findByItemIdInAndOrgIdAndBufferStartDateBeforeAndBufferEndDateAfter(
      List<String> itemId, String orgId, Date startDate, Date endDate);

  @Query(
      value =
          "select distinct on (item_id, org_id, uom) * From item_buffers_data where item_id IN ?1 AND org_id= ?2",
      countQuery =
          "SELECT COUNT(*) FROM (SELECT DISTINCT ON (item_id, org_id, uom) item_id, org_id, uom FROM item_buffers_data WHERE item_id IN ?1 AND org_id = ?2 ) AS subquery",
      nativeQuery = true)
  Page<ItemBufferEntity> findDistinctItemIdAndOrgIdAndUomByItemIdsAndOrgId(
      List<String> itemIds, String orgId, Pageable pageable);
}
