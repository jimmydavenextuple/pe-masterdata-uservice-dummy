package com.nextuple.postgres.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigConstants {

  public static final String ROUTING_DS = "routingDS";
  public static final String PRIMARY_DS = "primaryDS";
  public static final String READER_OR_PRIMARY_DS = "readerOrPrimaryDS";
}
