package com.nextuple.common.filter;

import com.nextuple.common.context.CurrentThreadContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthFilter implements Filter {
  @Value("${auth.filter-enabled}")
  private boolean filterEnabled;

  @Value("#{'${auth.roles}'.split(',')}")
  private List<String> roles;

  @Value("${auth.issuer}")
  private String issuer;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    Filter.super.init(filterConfig);
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    log.debug("-----Inside auth filter-----");
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;

    if (filterEnabled) {
      try {

        // Clean previous context
        CurrentThreadContext.cleanLogContext();
        MDC.clear();

        String authorizationHeaderValue = httpServletRequest.getHeader("Authorization");
        String token = extractJWTToken(authorizationHeaderValue);

        Jwt<Header, Claims> claims = getAllClaimsFromToken(token);
        log.debug(
            "--------Roles extracted from claims: {}------",
            claims.getBody().get("role").toString());
        log.debug("--------Issuer extracted from claims: {}------", claims.getBody().getIssuer());

        // Role checks
        Object roleClaims = claims.getBody().get("role");
        boolean roleVerified = verifyRoles(roleClaims);

        // Issuer checks
        String issuerString = claims.getBody().getIssuer();
        boolean issuerVerified = issuerString.equals(issuer);

        if (roleVerified && issuerVerified) {
          log.debug("All checks passed");
          CurrentThreadContext.getLogContext().setAuthorizationHeader(authorizationHeaderValue);
          chain.doFilter(request, response);
        } else {
          throw new AuthFilterException("Verification failed");
        }
      } catch (AuthFilterException e) {
        log.error("Authentication failed");
        throw e;
      } catch (Exception e) {
        log.error("Error while authenticating the request");
        throw e;
      }
    } else {
      chain.doFilter(request, response);
    }
  }

  private boolean verifyRoles(Object roleClaims) {
    boolean verified = false;
    if (roleClaims instanceof Collection) {
      for (String role : (List<String>) roleClaims) {
        if (roles.contains(role)) {
          verified = true;
          break;
        }
      }
    } else if (roleClaims instanceof String) {
      verified = roles.contains(roleClaims);
    }
    return verified;
  }

  private Jwt<Header, Claims> getAllClaimsFromToken(String token) {
    int i = token.lastIndexOf('.');
    String withoutSignature = token.substring(0, i + 1);
    return Jwts.parser().parseClaimsJwt(withoutSignature);
  }

  private String extractJWTToken(String authorizationHeaderValue) {
    if (!ObjectUtils.isEmpty(authorizationHeaderValue)) {
      String tokenString = authorizationHeaderValue;
      if (authorizationHeaderValue.startsWith("Bearer ")) {
        tokenString = authorizationHeaderValue.substring("Bearer ".length());
      }
      return tokenString;
    }
    return null;
  }

  @Override
  public void destroy() {
    Filter.super.destroy();
  }
}
