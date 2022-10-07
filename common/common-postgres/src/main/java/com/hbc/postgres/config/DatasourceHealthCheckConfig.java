package com.hbc.postgres.config;

import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthContributorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthIndicatorProperties;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatasourceHealthCheckConfig extends DataSourceHealthContributorAutoConfiguration {

  public DatasourceHealthCheckConfig(
      ObjectProvider<DataSourcePoolMetadataProvider> metadataProviders) {
    super(metadataProviders);
  }

  @Override
  public HealthContributor dbHealthContributor(
      Map<String, DataSource> dataSources,
      DataSourceHealthIndicatorProperties dataSourceHealthIndicatorProperties) {
    // remove the required datasource from the dataSources map by its name
    if (dataSources.containsKey(ConfigConstants.READER_OR_PRIMARY_DS)) {
      dataSources.remove(ConfigConstants.ROUTING_DS);
      dataSources.remove(ConfigConstants.PRIMARY_DS);

    } else {
      if (dataSources.containsKey(ConfigConstants.PRIMARY_DS))
        dataSources.remove(ConfigConstants.ROUTING_DS);
    }
    return super.dbHealthContributor(dataSources, dataSourceHealthIndicatorProperties);
  }
}
