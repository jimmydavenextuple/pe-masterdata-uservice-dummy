/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.persistence.mapper;

import com.nextuple.postgres.mapper.DomainToEntityMapper;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingRulesConfigurationDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.key.SourcingRulesConfigurationDomainKey;
import com.nextuple.promise.sourcing.rule.persistence.entity.SourcingRulesConfigurationEntity;
import com.nextuple.promise.sourcing.rule.persistence.entity.key.SourcingRulesConfigurationKey;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    componentModel = "spring")
public interface SourcingRulesConfigurationEntityMapper
    extends DomainToEntityMapper<
        SourcingRulesConfigurationDomainDto,
        SourcingRulesConfigurationDomainKey,
        SourcingRulesConfigurationEntity,
        SourcingRulesConfigurationKey> {}
