/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.domain.repository;

import com.nextuple.jobs.consumers.domain.entity.JobRecordEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRecordRepository extends JpaRepository<JobRecordEntity, String> {

  @Query(
      value =
          "SELECT * FROM job_records "
              + "WHERE org_id = CAST(?1 AS TEXT) "
              + "and job_id = CAST(?2 AS TEXT) "
              + "and (?3 is null or status = CAST(?3 AS TEXT)) ",
      nativeQuery = true)
  List<JobRecordEntity> findJobRecordsByFilters(String orgId, String jobId, String status);

  @Query(
      value =
          "SELECT * FROM job_records "
              + "WHERE job_id = CAST(?1 AS TEXT) "
              + "and org_id = CAST(?2 AS TEXT) "
              + "and (status = CAST(?3 AS TEXT)) ",
      nativeQuery = true)
  Page<JobRecordEntity> findJobRecordsByJobParam(
      String jobId, String orgId, String status, Pageable pageableElement);
}
