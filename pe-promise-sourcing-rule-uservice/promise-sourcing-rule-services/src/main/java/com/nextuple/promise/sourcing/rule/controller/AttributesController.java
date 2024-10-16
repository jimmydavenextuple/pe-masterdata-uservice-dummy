package com.nextuple.promise.sourcing.rule.controller;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.AttributeValueRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.AddAttributeValueResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.AttributeValueResponse;
import com.nextuple.promise.sourcing.rule.controller.docs.AddValueToAttributeDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.DeleteAttributeValueDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetAttributeValueDoc;
import com.nextuple.promise.sourcing.rule.service.AttributeService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ui")
@RequiredArgsConstructor
@Tag(name = "Attributes APIs")
public class AttributesController {
  private static final Logger logger = LoggerFactory.getLogger(AttributesController.class);
  private final AttributeService attributeService;

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      value = "/attribute-value/{orgId}/{attributeName}")
  @AddValueToAttributeDoc
  public ResponseEntity<BaseResponse<AddAttributeValueResponse>> saveValueToAttribute(
      @NotNull(message = "OrgId cannot be null")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull(message = "Attribute name cannot be null")
          @PathVariable
          @Parameter(description = "Name of the sourcing attribute", example = "productClass")
          String attributeName,
      @Valid @RequestBody AttributeValueRequest attributeValueRequest)
      throws PromiseEngineException, CommonServiceException {
    var attributeResponse =
        attributeService.addValueToAttribute(orgId, attributeName, attributeValueRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Attribute Name saved successfully.")
            .payload(attributeResponse)
            .build());
  }

  @GetMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      value = "attribute-value/{orgId}/{attributeName}")
  @GetAttributeValueDoc
  public ResponseEntity<BaseResponse<AttributeValueResponse>> getAttributeValues(
      @NotNull(message = "OrgId cannot be null")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull(message = "Attribute name cannot be null")
          @PathVariable
          @Parameter(description = "Name of the sourcing attribute", example = "productClass")
          String attributeName)
      throws PromiseEngineException, CommonServiceException {
    var attributeValueResponse = attributeService.getAttributeValues(orgId, attributeName);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Attribute values successfully fetched!")
            .payload(attributeValueResponse)
            .build());
  }

  @DeleteMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      value = "/attribute-value/{orgId}/{attributeName}/{value}")
  @DeleteAttributeValueDoc
  public ResponseEntity<BaseResponse<AddAttributeValueResponse>> deleteAttributeValue(
      @NotNull(message = "OrgId cannot be null")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull(message = "Attribute name cannot be null")
          @PathVariable
          @Parameter(description = "Name of the sourcing attribute", example = "productClass")
          String attributeName,
      @NotNull(message = "Attribute value cannot be null")
          @PathVariable
          @Parameter(description = "Value of the sourcing attribute", example = "Appliances")
          String value)
      throws PromiseEngineException, CommonServiceException {
    var attributeResponse = attributeService.deleteValueOfAttribute(orgId, attributeName, value);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Attribute Value deleted successfully.")
            .payload(attributeResponse)
            .build());
  }
}
