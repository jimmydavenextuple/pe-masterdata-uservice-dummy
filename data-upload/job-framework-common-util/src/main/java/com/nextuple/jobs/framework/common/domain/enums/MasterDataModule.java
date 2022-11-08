package com.nextuple.jobs.framework.common.domain.enums;

public enum MasterDataModule {
  TRANSIT("TRANSIT", "Transit"),
  NODE_CARRIER("NODE_CARRIER", "Node Carrier"),
  POSTAL_CODE_TIMEZONE("POSTAL_CODE_TIMEZONE", "Postal Code Timezone"),
  NODE_CALENDER("CALENDER", "Calendar"),
  NODE("NODE", "Node");

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
