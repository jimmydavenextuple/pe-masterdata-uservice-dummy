/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postgres.config;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.SimpleAutowireCandidateResolver;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthIndicatorProperties;
import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.jdbc.metadata.CompositeDataSourcePoolMetadataProvider;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Configuration
public class DatasourceHealthCheckConfig {

  private final DataSourcePoolMetadataProvider poolMetadataProvider;

  public DatasourceHealthCheckConfig(
      ObjectProvider<DataSourcePoolMetadataProvider> metadataProviders) {
    this.poolMetadataProvider =
        new CompositeDataSourcePoolMetadataProvider(metadataProviders.orderedStream().toList());
  }

  @Bean
  @Primary
  public HealthContributor dbHealthContributor(
      ConfigurableListableBeanFactory beanFactory, DataSourceHealthIndicatorProperties properties) {

    Map<String, DataSource> dataSources =
        SimpleAutowireCandidateResolver.resolveAutowireCandidates(
            beanFactory, DataSource.class, false, true);

    if (dataSources.containsKey(ConfigConstants.READER_OR_PRIMARY_DS)) {
      dataSources.remove(ConfigConstants.ROUTING_DS);
      dataSources.remove(ConfigConstants.PRIMARY_DS);
    } else {
      dataSources.remove(ConfigConstants.ROUTING_DS);
    }

    if (properties.isIgnoreRoutingDataSources()) {
      dataSources =
          dataSources.entrySet().stream()
              .filter(entry -> !isRoutingDataSource(entry.getValue()))
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    return createContributor(dataSources);
  }

  private HealthContributor createContributor(Map<String, DataSource> dataSources) {
    if (dataSources.isEmpty()) {
      throw new IllegalStateException("No DataSources available for health check");
    }

    if (dataSources.size() == 1) {
      return createIndicator(dataSources.values().iterator().next());
    }

    return CompositeHealthContributor.fromMap(dataSources, this::createIndicator);
  }

  private HealthContributor createIndicator(DataSource dataSource) {
    if (isRoutingDataSource(dataSource)) {
      AbstractRoutingDataSource routing = extractRoutingDataSource(dataSource);
      Map<String, DataSource> routedDataSources =
          routing.getResolvedDataSources().entrySet().stream()
              .collect(
                  Collectors.toMap(
                      e -> Objects.toString(e.getKey(), "unnamed"), Map.Entry::getValue));
      return CompositeHealthContributor.fromMap(routedDataSources, this::createIndicator);
    }

    DataSourcePoolMetadata metadata = poolMetadataProvider.getDataSourcePoolMetadata(dataSource);
    String validationQuery = metadata != null ? metadata.getValidationQuery() : null;
    return new DataSourceHealthIndicator(dataSource, validationQuery);
  }

  private boolean isRoutingDataSource(DataSource ds) {
    if (ds instanceof AbstractRoutingDataSource) return true;
    try {
      return ds.isWrapperFor(AbstractRoutingDataSource.class);
    } catch (SQLException e) {
      return false;
    }
  }

  private AbstractRoutingDataSource extractRoutingDataSource(DataSource ds) {
    if (ds instanceof AbstractRoutingDataSource rds) return rds;
    try {
      return ds.unwrap(AbstractRoutingDataSource.class);
    } catch (SQLException e) {
      throw new IllegalStateException("Failed to unwrap RoutingDataSource", e);
    }
  }
}
