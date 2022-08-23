package com.hbc.jobs.framework.common.domain.enums;

public enum MasterDataModule {
  TRANSIT("TRANSIT", "Transit"),
  NODE_CARRIER("NODE_CARRIER", "Node Carrier");

  private final String moduleName;
  private final String displayName;

  MasterDataModule(String moduleName, String displayName) {
    this.moduleName = moduleName;
    this.displayName = displayName;
  }

  public String getModuleName() {
    return moduleName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
