package com.nextuple.postgres.config;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDS extends AbstractRoutingDataSource {

  public RoutingDS(DataSource writer, DataSource reader) {
    Map<Object, Object> dataSources = new HashMap<>();
    dataSources.put("writer", writer);
    dataSources.put("reader", reader);

    setTargetDataSources(dataSources);
  }

  @Override
  protected Object determineCurrentLookupKey() {
    return ReadOnlyContext.isReadOnly() ? "reader" : "writer";
  }
}
