package com.nextuple.common.properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApplicationPropertiesTest {

  @Test
  void validationTest() {
    ApplicationProperties property1 = new ApplicationProperties();
    property1.setName("name1");
    property1.setContactEmail("contactEmail");
    property1.setContactName("contactName");
    property1.setContactWebsite("contactWebsite");

    ApplicationProperties property2 = new ApplicationProperties();
    property2.setName("name2");
    property2.setTitle("title");
    property2.setVersion("version");

    Assertions.assertEquals("contactName", property1.getContactName());
    Assertions.assertEquals("contactEmail", property1.getContactEmail());
    Assertions.assertEquals("contactWebsite", property1.getContactWebsite());

    Assertions.assertEquals("Nextuple Support Team", property2.getContactName());
    Assertions.assertEquals("support@nextuple.com", property2.getContactEmail());
    Assertions.assertEquals("https://www.nextuple.com", property2.getContactWebsite());
    Assertions.assertEquals("name2", property2.getName());
    Assertions.assertEquals("title", property2.getTitle());
    Assertions.assertEquals("version", property2.getVersion());
    Assertions.assertNotNull(property2.getProperties());
  }
}
