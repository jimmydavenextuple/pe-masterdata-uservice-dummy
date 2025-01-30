package com.nextuple.common.interceptor;

import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.exception.CommonServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

@Component
@NoArgsConstructor
public class RestRequestInterceptor implements HandlerInterceptor {

  private static final String ORG_ID = "orgId";
  private boolean tenantCheckEnabled;

  public RestRequestInterceptor(boolean tenantCheckEnabled) {
    this.tenantCheckEnabled = tenantCheckEnabled;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    // Exclude OPTIONS requests - Required for disabling CORS
    if (request.getMethod().equals("OPTIONS")) {
      return false;
    }

    if (!Boolean.TRUE.equals(tenantCheckEnabled)
        || request.getRequestURI().contains("/get-all-cache-keys")) return true;
    String tenantId = CurrentThreadContext.getLogContext().getTenantId();
    if (Objects.isNull(tenantId))
      throw new CommonServiceException(
          "TenantId missing! and offending URL: " + request.getRequestURL(),
          HttpStatus.FORBIDDEN,
          1012,
          null);

    Map<String, String> map =
        (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    if (Objects.isNull(map)) {
      throw new CommonServiceException("Invalid Params!", HttpStatus.FORBIDDEN, 1011, null);
    }
    if (map.containsKey(ORG_ID)) {
      String pathVariableOrgId = map.get(ORG_ID);
      if (!tenantId.equals(pathVariableOrgId)) {
        throw new CommonServiceException("OrgId mismatch!", HttpStatus.FORBIDDEN, 1011, null);
      }
    }

    Map<String, String[]> queryParametersMap = request.getParameterMap();

    if (queryParametersMap.containsKey(ORG_ID)) {
      String[] queryParameterOrgId = queryParametersMap.get(ORG_ID);
      if (!tenantId.equals(queryParameterOrgId[0])) {
        throw new CommonServiceException("OrgId mismatch!", HttpStatus.FORBIDDEN, 1011, null);
      }
    }
    return true;
  }
}
