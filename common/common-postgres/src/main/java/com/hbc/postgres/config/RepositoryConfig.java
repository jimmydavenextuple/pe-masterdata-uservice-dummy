package com.hbc.postgres.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(enableDefaultTransactions = false)
@EnableTransactionManagement
@ConditionalOnProperty(name = "spring.datasource.url")
public class RepositoryConfig extends HikariConfig {

  @Value("${spring.datasource.url}")
  private String databaseUrl;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  @Value("${spring.replica.datasource.url:null}")
  private String replicaDatabaseUrl;

  @Value("${spring.replica.datasource.required:false}")
  private Boolean replicaReq;

  @Value("${spring.replica.datasource.username:null}")
  private String replicaUsername;

  @Value("${spring.replica.datasource.password:null}")
  private String replicaPassword;

  @Bean("primaryProperties")
  @ConfigurationProperties(prefix = "spring.datasource.hikari")
  public Properties primaryProperties() {
    return new Properties();
  }

  @Bean("replicaProperties")
  @ConfigurationProperties(prefix = "spring.replica.datasource.hikari")
  public Properties replicaProperties() {
    return new Properties();
  }

  @Bean
  public DataSource routingDataSource() {
    var primaryDataSource = dataSource(false, false);
    var replicaDataSource = replicaDataSource(true, true);
    if (replicaDataSource == null) {
      replicaDataSource = primaryDataSource;
    }
    return new RoutingDS(primaryDataSource, replicaDataSource);
  }

  private DataSource dataSource(boolean readOnly, boolean isAutoCommit) {
    var config = new HikariConfig(primaryProperties());
    config.setJdbcUrl(databaseUrl);
    config.setUsername(username);
    config.setPassword(password);
    config.setReadOnly(readOnly);
    config.setAutoCommit(isAutoCommit);
    return new HikariDataSource(config);
  }

  private DataSource replicaDataSource(boolean readOnly, boolean isAutoCommit) {
    if (Boolean.FALSE.equals(replicaReq)) {
      return null;
    }
    var config = new HikariConfig(replicaProperties());
    config.setJdbcUrl(replicaDatabaseUrl);
    config.setUsername(replicaUsername);
    config.setPassword(replicaPassword);
    config.setReadOnly(readOnly);
    config.setAutoCommit(isAutoCommit);
    return new HikariDataSource(config);
  }
}
