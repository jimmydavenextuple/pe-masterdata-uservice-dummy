package com.nextuple.common.config;

import com.nextuple.common.properties.ApplicationProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class OpenAPIConfigTest {

  @InjectMocks OpenAPIConfig openAPIConfig;

  @BeforeEach
  void init() {
    ApplicationProperties property = new ApplicationProperties();
    property.setName("name1");
    property.setContactEmail("contactEmail");
    property.setContactName("contactName");
    property.setContactWebsite("contactWebsite");
    property.setName("name2");
    property.setTitle("title");
    property.setVersion("version");
    ApplicationProperties.Properties properties = new ApplicationProperties.Properties();
    properties.put("group", "promising");
    property.setProperties(properties);
    ReflectionTestUtils.setField(openAPIConfig, "applicationProperties", property);
  }

  @Test
  void serviceInfoTest() {
    Info info = openAPIConfig.serviceInfo();
    Assertions.assertEquals("title", info.getTitle());
    Assertions.assertEquals("version", info.getVersion());
    Assertions.assertEquals("contactName", info.getContact().getName());
    Assertions.assertEquals("promising", info.getExtensions().get("x-group"));
  }

  @Test
  void openAPITest() {
    Info info = openAPIConfig.serviceInfo();
    OpenAPI openAPI = openAPIConfig.openAPI(info);
    Assertions.assertNotNull(openAPI);
    Assertions.assertEquals(info, openAPI.getInfo());
  }
}
