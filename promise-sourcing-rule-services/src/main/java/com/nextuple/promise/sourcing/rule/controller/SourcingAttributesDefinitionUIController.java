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

@Validated
@RestController
@RequestMapping("/ui/sourcing-attributes-definition")
@RequiredArgsConstructor
@Tag(name = "Sourcing Attributes Definition Dashboard APIs")
public class SourcingAttributesDefinitionUIController {
  public static final Logger logger =
      LoggerFactory.getLogger(SourcingAttributesDefinitionUIController.class);
  private final SourcingAttributesDefinitionService sourcingAttributesDefinitionService;

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
