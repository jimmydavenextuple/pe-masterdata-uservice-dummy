package com.hbc.service.inventory;

import com.hbc.service.inventory.domain.entity.ServiceOptionInventoryTypeEntity;
import com.hbc.service.inventory.domain.inbound.ServiceInventoryRequest;
import com.hbc.service.inventory.domain.outbound.ServiceInventoryDto;

public class TestUtil {

  public static final String ORG_ID = "org-1";

  public static final String SERVICE_OPTION = "serviceOption-1";
  public static final String INVENTORY_TYPE = "inventoryType-1";

  public ServiceInventoryRequest getServiceInventoryRequest() {
    return ServiceInventoryRequest.builder()
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .inventoryType(INVENTORY_TYPE)
        .build();
  }

  public ServiceInventoryDto getServiceInventoryDto() {
    return ServiceInventoryDto.builder()
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .inventoryType(INVENTORY_TYPE)
        .build();
  }

  public ServiceOptionInventoryTypeEntity getServiceOptionInventoryTypeEntity() {
    return ServiceOptionInventoryTypeEntity.builder()
        .orgId(ORG_ID)
        .serviceOption(SERVICE_OPTION)
        .inventoryType(INVENTORY_TYPE)
        .build();
  }
}
