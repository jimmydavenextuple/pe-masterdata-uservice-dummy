/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.pe.configs;

import java.util.Map;
import java.util.Set;

public interface ITenantConfig {
  String getServiceOptions();

  Set<String> getServiceOptionsList();

  String getServiceOptionInventoryTypeMapping();

  String getPublishEddResponseOnPage();

  Set<String> getPublishEddResponseOnPageList();

  String getDefaultCarrierPriority();

  String getLogSuppressionServiceOptions();

  Set<String> getLogSuppressionServiceOptionsList();

  Integer getNumberOfSolutions();

  Integer getNumberOfNodes();

  Set<String> getAllowedPagesListForPublishingEvent();

  Map<String, Boolean> getPublishEnabledMap();

  Map<String, String> getLogLevelMap();

  Map<String, Boolean> getConsoleLogListenEnabledMap();

  Integer getNoOfLineSolutionsRequired();

  Integer getLineThreshold();

  String getSortBy();

  Integer getFinalSolutionCap();

  Integer getTopSolutionsCount();

  Integer getMaxSolutions();

  Integer getBufferHorizonDays();

  String getProcessingTimeComputation();

  String getNodeWorkingHours();

  String getProcessingTimeMLOverrideClass();

  String getProcessingTimeMLMapper();

  Boolean getIsCutoffApplied();

  Boolean getItemBufferEnabled();

  Boolean getPromisingIntermediateEventsEnabled();
}
