package com.hbc.postgres.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthContributorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthIndicatorProperties;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;


@Configuration
public class DatasourceHealthCheckConfig extends DataSourceHealthContributorAutoConfiguration {


    public DatasourceHealthCheckConfig(ObjectProvider<DataSourcePoolMetadataProvider> metadataProviders) {
        super(metadataProviders);
    }

    @Override
    public HealthContributor dbHealthContributor(Map<String, DataSource> dataSources,
                                                 DataSourceHealthIndicatorProperties dataSourceHealthIndicatorProperties) {
        // remove the required datasource from the dataSources map by its name
        if(dataSources.containsKey("readerAndPrimaryDS"))
        {
            dataSources.remove("routingDS");
            dataSources.remove("primaryDS");

        }
        
        else
        {
           if(dataSources.containsKey("primaryDS")) dataSources.remove("routingDS");
        }
        return super.dbHealthContributor(dataSources, dataSourceHealthIndicatorProperties);
    }
}
