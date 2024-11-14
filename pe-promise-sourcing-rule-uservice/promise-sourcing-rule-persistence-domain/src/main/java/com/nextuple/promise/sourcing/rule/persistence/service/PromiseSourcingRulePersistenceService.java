/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.service;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.service.DomainPersistenceService;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.persistence.domain.PromiseSourcingRuleDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.PromiseSourcingRuleDomainKey;
import java.util.List;

public interface PromiseSourcingRulePersistenceService
    extends DomainPersistenceService<PromiseSourcingRuleDomainDto, PromiseSourcingRuleDomainKey> {

  List<PromiseSourcingRuleDomainDto> fetchSourcingRule(FetchPromiseSourcingRuleRequest baseRequest)
      throws PromiseEngineException;

  List<PromiseSourcingRuleDomainDto> fetchSourcingRule(
      String serviceOption, String orgId, String allocationId, String destinationGeoZone)
      throws PromiseEngineException;

  PromiseSourcingRuleDomainDto savePromiseSourcingRule(
      PromiseSourcingRuleDomainDto promiseSourcingRuleDomainDto) throws PromiseEngineException;

  PromiseSourcingRuleDomainDto getPromiseSourcingRule(
      String orgId,
      String serviceOption,
      String destinationGeoZone,
      String allocationRuleId,
      int priority)
      throws PromiseEngineException;

  List<PromiseSourcingRuleDomainDto> getPromiseSourcingRulesByOrgId(String orgId)
      throws PromiseEngineException;

  List<PromiseSourcingRuleDomainDto> getPromiseSourcingRulesByPriority(int priority)
      throws PromiseEngineException;

  PromiseSourcingRuleDomainDto deletePromiseSourcingRule(
      PromiseSourcingRuleDomainDto promiseSourcingRuleDomainDto) throws PromiseEngineException;
}
