package com.nextuple.common.util;

import static com.nextuple.common.constants.CommonConstants.SERVER_UNAVAILABLE_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.nextuple.common.TestUtil;
import com.nextuple.common.exception.ServiceUnavailableException;
import feign.FeignException;
import java.net.SocketException;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommonUtilTest {

  @DisplayName("Should be able to return empty based on the value in the parseDNSName function")
  @Test
  void parseDNSNameEmptyTest() {
    Optional<String> expected = Optional.empty();
    Optional<String> actual = CommonUtil.parseDNSName("");
    assertEquals(expected, actual, "DNS Name");
  }

  @DisplayName("Should return 'xyz' based on the value in the parseDNSName function")
  @Test
  void parseDNSNameTest() {
    Optional<String> expected = Optional.of("xyz");
    Optional<String> actual = CommonUtil.parseDNSName("xyz.nextuple.com");
    assertEquals(expected, actual, "DNS Name");
  }

  @DisplayName("Should not return 'abc' based on the value in the parseDNSName function")
  @Test
  void parseDNSNameTestWrong() {
    Optional<String> expected = Optional.of("abc");
    Optional<String> actual = CommonUtil.parseDNSName("xyz.nextuple.com");
    assertNotEquals(expected, actual, "DNS Name");
  }

  @DisplayName(
      "Should return 'xyz' based on the value in the parseDNSName function (includes hyphen)")
  @Test
  void parseDNSNameHyphenTest() {
    Optional<String> expected = Optional.of("xyz");
    Optional<String> actual = CommonUtil.parseDNSName("xyz-dev.nextuple.com");
    assertEquals(expected, actual, "DNS Name");
  }

  @DisplayName(
      "Should not return 'abc' based on the value in the parseDNSName function (includes hyphen)")
  @Test
  void parseDNSNameHyphenTestWrong() {
    Optional<String> expected = Optional.of("abc");
    Optional<String> actual = CommonUtil.parseDNSName("xyz-dev.nextuple.com");
    assertNotEquals(expected, actual, "DNS Name");
  }

  @DisplayName("Should return empty based on the value in the parseEnvironmentName function")
  @Test
  void parseEnvironmentNameEmptyTest() {
    Optional<String> expected = Optional.empty();
    Optional<String> actual = CommonUtil.parseEnvironmentName("");
    assertEquals(expected, actual, "DNS Name");
  }

  @DisplayName(
      "Should return 'prod' based on the value in the parseEnvironmentName function since there is no hyphen")
  @Test
  void parseEnvironmentNameNoHyphenTest() {
    Optional<String> expected = Optional.of("prod");
    Optional<String> actual = CommonUtil.parseEnvironmentName("xyz.nextuple.com");
    assertEquals(expected, actual, "DNS Name");
  }

  @DisplayName("Should return 'dev' based on the value in the parseEnvironmentName function")
  @Test
  void parseEnvironmentNameHyphenTest() {
    Optional<String> expected = Optional.of("dev");
    Optional<String> actual = CommonUtil.parseEnvironmentName("xyz-dev.nextuple.com");
    assertEquals(expected, actual, "DNS Name");
  }

  @DisplayName("Should not return 'prod' based on the value in the parseEnvironmentName function")
  @Test
  void parseEnvironmentNameHyphenTestWrong() {
    Optional<String> expected = Optional.of("prod");
    Optional<String> actual = CommonUtil.parseEnvironmentName("xyz-dev.nextuple.com");
    assertNotEquals(expected, actual, "DNS Name");
  }

  @Test
  @DisplayName("Handle Feign Connection Exception with valid exception")
  void handleFeignConnectionExceptionTest() {
    FeignException ex =
        (FeignException) TestUtil.getConnectionRefusedFeignException(new SocketException());
    ServiceUnavailableException e =
        Assertions.assertThrows(
            ServiceUnavailableException.class, () -> CommonUtil.handleFeignConnectionException(ex));
    Assertions.assertEquals(SERVER_UNAVAILABLE_ERROR_MESSAGE, e.getMessage());
  }

  @Test
  @DisplayName("Handle Feign Connection Exception with invalid exception")
  void handleFeignConnectionExceptionTestInvalid() {
    FeignException ex =
        (FeignException)
            TestUtil.getConnectionRefusedFeignException(new ServiceUnavailableException());
    Assertions.assertThrows(
        FeignException.class, () -> CommonUtil.handleFeignConnectionException(ex));
  }
}
