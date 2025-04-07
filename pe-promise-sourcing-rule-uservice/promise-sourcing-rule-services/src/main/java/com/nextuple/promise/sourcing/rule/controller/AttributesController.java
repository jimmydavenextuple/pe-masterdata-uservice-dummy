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

/**
 * Controller for managing sourcing attribute values.
 *
 * <p>This controller provides APIs for adding, retrieving, and deleting values for sourcing
 * attributes associated with specific organizations. These operations allow users to manage the
 * attribute values for various sourcing attributes in their organization.
 *
 * <p>The controller is tagged with "Attributes APIs" for easy categorization in the API
 * documentation.
 */
@RestController
@RequestMapping("/ui")
@RequiredArgsConstructor
@Tag(name = "Attributes APIs")
public class AttributesController {
  private static final Logger logger = LoggerFactory.getLogger(AttributesController.class);
  private final AttributeService attributeService;

  /**
   * Adds a value to an attribute for a specific organization.
   *
   * <p>This method processes a POST request to add a new value to an existing sourcing attribute
   * identified by its name and organization ID.
   *
   * @param orgId The unique identifier of the organization.
   * @param attributeName The name of the sourcing attribute.
   * @param attributeValueRequest The request body containing the value to be added to the
   *     attribute.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the added attribute
   *     value details.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
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

  /**
   * Retrieves all values associated with an attribute for a specific organization.
   *
   * <p>This method processes a GET request to fetch the values of a sourcing attribute identified
   * by its name and organization ID.
   *
   * @param orgId The unique identifier of the organization.
   * @param attributeName The name of the sourcing attribute.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with the list of attribute
   *     values.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
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

  /**
   * Deletes a specific value from an attribute for a specific organization.
   *
   * <p>This method processes a DELETE request to remove a value from a sourcing attribute
   * identified by its name and organization ID.
   *
   * @param orgId The unique identifier of the organization.
   * @param attributeName The name of the sourcing attribute.
   * @param value The value of the sourcing attribute to be deleted.
   * @return A {@link ResponseEntity} containing a {@link BaseResponse} with details of the deleted
   *     attribute value.
   * @throws PromiseEngineException If an error occurs related to the promise engine.
   * @throws CommonServiceException If a general service-related error occurs while processing the
   *     request.
   */
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
