package com.nextuple.common.util;

import static com.nextuple.common.constants.CommonConstants.SERVER_UNAVAILABLE_ERROR_MESSAGE;
import static com.nextuple.common.util.BooleanUtil.isFeignConnectionException;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.ServiceUnavailableException;
import feign.FeignException;
import java.util.Optional;
import org.springframework.util.ObjectUtils;

public class CommonUtil {

  private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

  private CommonUtil() {
    // Everyone should be using static methods only
  }

  /**
   * Parse Tenant DNS Name from host string For example, if host is xyz.nextuple.com than method
   * will return xyz
   *
   * <p>It will also remove environment variable, like for xyz-[env].nextuple.com method will return
   * xyz
   *
   * @param host
   * @return
   */
  public static Optional<String> parseDNSName(String host) {
    if (!ObjectUtils.isEmpty(host)) {
      // Extract sub domain
      String[] hostParts = host.split("\\.");
      if (hostParts.length > 0) {
        String subDomain = hostParts[0];
        // Remove environment slug
        String[] subDomainParts = subDomain.split("-");
        if (subDomainParts.length > 0) {
          return Optional.of(subDomainParts[0]);
        }
      }
    }
    return Optional.empty();
  }

  /**
   * Parse environment name from host string For example, if host is xyz-env.nextuple.com than
   * method will return env and in case of xyz.nextuple.com will return prod
   *
   * @param host
   * @return
   */
  public static Optional<String> parseEnvironmentName(String host) {
    if (!ObjectUtils.isEmpty(host)) {
      // Extract sub domain
      String[] hostParts = host.split("\\.");
      if (hostParts.length > 0) {
        String subDomain = hostParts[0];
        String[] subDomainParts = subDomain.split("-");
        if (subDomainParts.length > 1) {
          return Optional.of(subDomainParts[1]);
        } else {
          return Optional.of("prod");
        }
      }
    }
    return Optional.empty();
  }

  public static void handleFeignConnectionException(FeignException e) throws FeignException {
    if (isFeignConnectionException(e)) {
      logger.error(SERVER_UNAVAILABLE_ERROR_MESSAGE, e);
      throw new ServiceUnavailableException();
    }
    throw e;
  }
}
