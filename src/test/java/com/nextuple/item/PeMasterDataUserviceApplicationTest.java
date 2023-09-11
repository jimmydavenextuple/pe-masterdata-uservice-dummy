package com.nextuple.item;

import com.nextuple.masterdata.PeMasterDataUserviceApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class PeMasterDataUserviceApplicationTest {
  @Test
  void mainTest() {
    MockedStatic<SpringApplication> mock = Mockito.mockStatic(SpringApplication.class);
    mock.when(
            (MockedStatic.Verification)
                SpringApplication.run(PeMasterDataUserviceApplication.class, new String[] {}))
        .thenReturn(null);
    PeMasterDataUserviceApplication.main(new String[] {});
    Assertions.assertNull(
        SpringApplication.run(PeMasterDataUserviceApplication.class, new String[] {}));
  }
}
