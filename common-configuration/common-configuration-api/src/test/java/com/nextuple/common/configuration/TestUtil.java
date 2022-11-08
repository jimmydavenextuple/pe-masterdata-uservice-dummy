package com.nextuple.common.configuration;

import com.nextuple.common.configuration.api.domain.dto.CommonConfigurationDto;
import com.nextuple.common.configuration.api.domain.inbound.CreateCommonConfigurationRequest;
import com.nextuple.common.configuration.domain.entity.CommonConfiguration;
import com.nextuple.common.configuration.mapper.CommonConfigMapper;
import org.mapstruct.factory.Mappers;

public class TestUtil {
  public static final String ORGID = "BAY";
  public static final String TYPE = "DELIVERY_METHOD";
  public static final String KEY = "SDND";
  public static final CommonConfigMapper INSTANCE = Mappers.getMapper(CommonConfigMapper.class);

  public CommonConfiguration getCommonConfiguration() {
    CommonConfiguration globalConfiguration = new CommonConfiguration();
    globalConfiguration.setKey(KEY);
    globalConfiguration.setType(TYPE);
    globalConfiguration.setOrgId(ORGID);
    globalConfiguration.setValue("PICK");
    return globalConfiguration;
  }

  public CreateCommonConfigurationRequest getCreateRequest() {
    return CreateCommonConfigurationRequest.builder()
        .orgId(ORGID)
        .type(TYPE)
        .key(KEY)
        .value("PICK")
        .build();
  }

  public CommonConfigurationDto getCommonConfigurationDto() {
    return INSTANCE.toCommonConfigurationDto(getCommonConfiguration());
  }
}
