package com.hbc.global.configuration;

import com.hbc.global.configuration.api.domain.dto.GlobalConfigurationDto;
import com.hbc.global.configuration.api.domain.inbound.CreateGlobalConfigurationRequest;
import com.hbc.global.configuration.domain.entity.GlobalConfiguration;
import com.hbc.global.configuration.mapper.GlobalConfigMapper;
import org.mapstruct.factory.Mappers;

public class TestUtil {
  public static final String ORGID = "BAY";
  public static final String TYPE = "DELIVERY_METHOD";
  public static final String KEY = "SDND";
  public static final GlobalConfigMapper INSTANCE = Mappers.getMapper(GlobalConfigMapper.class);

  public GlobalConfiguration getGlobalConfiguration() {
    GlobalConfiguration globalConfiguration = new GlobalConfiguration();
    globalConfiguration.setKey(KEY);
    globalConfiguration.setType(TYPE);
    globalConfiguration.setOrgId(ORGID);
    globalConfiguration.setValue("PICK");
    return globalConfiguration;
  }

  public CreateGlobalConfigurationRequest getCreateRequest() {
    return CreateGlobalConfigurationRequest.builder()
        .orgId(ORGID)
        .type(TYPE)
        .key(KEY)
        .value("PICK")
        .build();
  }

  public GlobalConfigurationDto getGlobalConfigurationDto() {
    return INSTANCE.toGlobalConfigurationDto(getGlobalConfiguration());
  }
}
