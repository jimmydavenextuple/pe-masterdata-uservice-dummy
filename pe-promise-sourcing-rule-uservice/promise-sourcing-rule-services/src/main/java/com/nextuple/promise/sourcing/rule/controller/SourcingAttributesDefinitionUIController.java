package com.nextuple.promise.sourcing.rule.controller;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeDefinitionUIResponse;
import com.nextuple.promise.sourcing.rule.controller.docs.GetRequiredAndOptionalAttributeDoc;
import com.nextuple.promise.sourcing.rule.service.SourcingAttributesDefinitionService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing the sourcing attributes definition dashboard for organizations.
 *
 * <p>This controller provides an API to fetch the required and optional sourcing attribute
 * definitions for a specific organization. These attributes can be scoped to different areas such
 * as sourcing rules and optimization.
 *
 * <p>The controller is tagged with "Sourcing Attributes Definition Dashboard APIs" for easy
 * categorization in the API documentation.
 */
@Validated
@RestController
@RequestMapping("/ui/sourcing-attributes-definition")
@RequiredArgsConstructor
@Tag(name = "Sourcing Attributes Definition Dashboard APIs")
public class SourcingAttributesDefinitionUIController {
  public static final Logger logger =
      LoggerFactory.getLogger(SourcingAttributesDefinitionUIController.class);
  private final SourcingAttributesDefinitionService sourcingAttributesDefinitionService;

  /**
   * Retrieves required and optional sourcing attributes definitions for a specific organization.
   *
   * <p>Processes a GET request to fetch active sourcing attribute definitions based on the
   * organization ID and optionally the scope.
   *
   * @param orgId The unique identifier of the organization. Must not be null.
   * @param scope (Optional) The {@link SourcingAttributesDefinitionScopeEnum} specifying the scope
   *     of the attributes definition.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the required and
   *     optional attributes definitions.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a common service exception occurs during the process.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "/{orgId}")
  @GetRequiredAndOptionalAttributeDoc
  public ResponseEntity<BaseResponse<SourcingAttributeDefinitionUIResponse>>
      getReqAndOptAttributesByOrgId(
          @NotNull(message = "OrgId cannot be null")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              String orgId,
          @RequestParam(required = false)
              @Parameter(
                  description = "Scope of the attributes definition which is defined",
                  example = "SOURCING_RULE, OPTIMIZATION")
              SourcingAttributesDefinitionScopeEnum scope)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("Getting required and optional attributes for orgId");
    try {
      var allSourcingRulesResponses =
          sourcingAttributesDefinitionService.processGetActiveSourcingAttributeDefinitionForUI(
              orgId, scope);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Required and Optional Attributes fetched successfully.")
              .payload(allSourcingRulesResponses)
              .build());

    } catch (Exception e) {
      logger.error("Failed to fetch attributes!");
      throw e;
    }
  }
}
