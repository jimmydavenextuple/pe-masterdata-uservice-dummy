package com.hbc.common.filter;

import com.hbc.common.context.CurrentThreadContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthFilter implements Filter {
  private final AuthProperties authProperties;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    Filter.super.init(filterConfig);
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    log.debug("-----Inside auth filter-----");
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    log.debug("Request URL: {}", httpServletRequest.getRequestURL());

    if (authProperties.isFilterEnabled()
        && !httpServletRequest.getRequestURI().startsWith("/actuator")) {
      try {

        // Clean previous context
        CurrentThreadContext.cleanLogContext();
        MDC.clear();

        String authorizationHeaderValue = httpServletRequest.getHeader("Authorization");
        Optional<String> tokenString = extractJWTToken(authorizationHeaderValue);
        if (!tokenString.isPresent()) {
          throw new AuthFilterException("Authorization header value is empty or null");
        }
        Jwt<? extends Header, Claims> claims = getAllClaimsFromToken(tokenString.get());

        // Check all Claims
        boolean allVerified = verifyAllClaimsAndIssuer(claims);

        if (allVerified) {
          log.debug("All checks passed");
          CurrentThreadContext.getLogContext().setAuthorizationHeader(authorizationHeaderValue);
          chain.doFilter(request, response);
        } else {
          throw new AuthFilterException("Verification failed");
        }
      } catch (AuthFilterException e) {
        log.error(
            "Authentication failed for the request: {}", httpServletRequest.getRequestURL(), e);
        throw e;
      } catch (Exception e) {
        log.error("Error while getting the response", e);
        throw e;
      }
    } else {
      chain.doFilter(request, response);
    }
  }

  private boolean verifyAllClaimsAndIssuer(Jwt<? extends Header, Claims> claims) {
    try {
      // Claims check
      boolean claimsVerified = true;
      Map<String, String> claimsMap = authProperties.getClaims();
      for (String claim : claimsMap.keySet()) {
        String userPassedClaimValue = claims.getBody().get(claim).toString();
        log.debug("--------Claim {}: {}------", claim, userPassedClaimValue);
        String actualClaimValue = authProperties.getClaims().get(claim);
        if (!userPassedClaimValue.equals(actualClaimValue)) {
          claimsVerified = false;
          break;
        }
      }

      // Issuer check
      String issuerString = claims.getBody().getIssuer();
      log.debug("--------Issuer extracted from claims: {}------", issuerString);
      boolean issuerVerified = issuerString.equals(authProperties.getIssuer());
      return claimsVerified && issuerVerified;
    } catch (NullPointerException e) {
      throw new AuthFilterException("Required claims not found");
    }
  }

  private Jwt<? extends Header, Claims> getAllClaimsFromToken(String token) {
    int i = token.lastIndexOf('.');
    String withoutSignature = token.substring(0, i + 1);
    return Jwts.parser().parseClaimsJwt(withoutSignature);
  }

  private Optional<String> extractJWTToken(String authorizationHeaderValue) {
    if (!ObjectUtils.isEmpty(authorizationHeaderValue)) {
      String tokenString = authorizationHeaderValue;
      if (authorizationHeaderValue
          .toLowerCase(Locale.ROOT)
          .startsWith("Bearer ".toLowerCase(Locale.ROOT))) {
        tokenString = authorizationHeaderValue.substring("Bearer ".length());
      }
      return Optional.of(tokenString);
    }
    return Optional.empty();
  }

  @Override
  public void destroy() {
    Filter.super.destroy();
  }
}
