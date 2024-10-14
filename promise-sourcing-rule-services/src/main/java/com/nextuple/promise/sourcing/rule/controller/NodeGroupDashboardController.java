package com.nextuple.promise.sourcing.rule.controller;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.DeleteNodeGroupsRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdateSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupDetailWithPriorityResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupWithNodesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.edit.sourcing.rules.UpdateSourcingRuleConfigurationResponse;
import com.nextuple.promise.sourcing.rule.controller.docs.CreateNodeGroupDashboardDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.DeleteNodeGroupDashboardDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.EditNodeGroupDashboardDoc;
import com.nextuple.promise.sourcing.rule.service.NodeGroupService;
import com.nextuple.promise.sourcing.rule.service.SourcingRulesConfigurationService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ui")
@RequiredArgsConstructor
@Validated
@Tag(name = "Node Group Dashboard APIs")
public class NodeGroupDashboardController {
  private static final Logger logger = LoggerFactory.getLogger(NodeGroupDashboardController.class);
  private final NodeGroupService nodeGroupService;
  private final SourcingRulesConfigurationService sourcingRulesConfigurationService;

  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      value = "/node-priority/{orgId}")
  @CreateNodeGroupDashboardDoc
  public ResponseEntity<BaseResponse<NodeGroupWithNodesResponse>> createNodeGroup(
      @NotNull(message = "id cannot be null")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @RequestBody NodeGroupWithNodesResponse nodeGroupWithNodes)
      throws PromiseEngineException, CommonServiceException {
    try {
      NodeGroupWithNodesResponse response =
          nodeGroupService.createNodeGroupFromDashboard(orgId, nodeGroupWithNodes);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Nodegroup successfully added.")
              .payload(response)
              .build());
    } catch (Exception e) {
      logger.error("Failed to add node group", e);
      throw e;
    }
  }

  @PutMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      value = "/node-priority/{orgId}/{nodeGroupId}")
  @EditNodeGroupDashboardDoc
  public ResponseEntity<BaseResponse<NodeGroupWithNodesResponse>> editNodeGroup(
      @NotNull(message = "id cannot be null")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull(message = "nodeGroupId cannot be null")
          @PathVariable
          @Parameter(description = "Unique identifier for the Node Group.", example = "234")
          Long nodeGroupId,
      @RequestBody @Valid NodeGroupWithNodesResponse nodeGroupWithNodes)
      throws PromiseEngineException, CommonServiceException {
    try {
      NodeGroupWithNodesResponse response =
          nodeGroupService.editNodeGroupFromDashboard(orgId, nodeGroupId, nodeGroupWithNodes);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Nodegroup updated successfully.")
              .payload(response)
              .build());
    } catch (Exception e) {
      logger.error("Failed to update node group", e);
      throw e;
    }
  }

  @DeleteMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      value = "/node-priority/{orgId}/{nodeGroupId}")
  @DeleteNodeGroupDashboardDoc
  public ResponseEntity<BaseResponse<NodeGroupWithNodesResponse>> deleteNodeGroup(
      @NotNull(message = "id cannot be null")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @NotNull(message = "nodeGroupId cannot be null")
          @PathVariable
          @Parameter(description = "Unique identifier for the Node Group.", example = "234")
          Long nodeGroupId)
      throws PromiseEngineException, CommonServiceException {
    try {
      NodeGroupWithNodesResponse response =
          nodeGroupService.deleteNodeGroupAndAssociatedNodePriority(orgId, nodeGroupId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Nodegroup deleted successfully.")
              .payload(response)
              .build());
    } catch (Exception e) {
      logger.error("Failed to delete node group", e);
      throw e;
    }
  }

  @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/node-priority/{orgId}")
  @Operation(summary = "Delete Node Group", description = "Deletes the node group details.")
  @ApiResponse(
      responseCode = "200",
      description = "A 200 success code indicates that the node groups is deleted successfully.")
  public ResponseEntity<BaseResponse<String>> deleteNodeGroupsByOrgId(
      @NotNull(message = "id cannot be null")
          @PathVariable
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          String orgId,
      @RequestBody DeleteNodeGroupsRequest request)
      throws PromiseEngineException {
    try {
      nodeGroupService.deleteNodeGroupsAndAssociatedNodePriorityByOrgId(orgId, request);
      return ResponseEntity.ok(
          BaseResponse.builder().message("Nodegroup deleted successfully.").build());
    } catch (Exception e) {
      logger.error("Failed to delete node group", e);
      throw e;
    }
  }

  @GetMapping(
      value = "/node-group/{orgId}/node-priority",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Get Node Group With Priority",
      description = "Retrieves the node group details with priority.")
  @ApiResponse(
      responseCode = "200",
      description =
          "A 200 success code indicates that the node group details with priority is fetched successfully.")
  public ResponseEntity<BaseResponse<PageResponse<NodeGroupDetailWithPriorityResponse>>>
      getNodeGroupWithPriority(
          @NotNull(message = "orgId can't be null")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              String orgId,
          PageParams pageParams)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing get node group by id request");
    var nodeGroupResponse =
        nodeGroupService.fetchNodeGroupsByOrgIdWithNodePriority(orgId, pageParams);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node group successfully fetched")
            .payload(nodeGroupResponse)
            .build());
  }

  @PutMapping(
      value = "/sourcing-rules-configuration/{orgId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Hidden
  public ResponseEntity<BaseResponse<UpdateSourcingRuleConfigurationResponse>>
      editSourcingRuleConfiguration(
          @PathVariable String orgId,
          @RequestBody @Valid UpdateSourcingRuleRequest sourcingRuleEditRequest)
          throws PromiseEngineException, CommonServiceException {
    UpdateSourcingRuleConfigurationResponse response =
        sourcingRulesConfigurationService.editSourcingRuleConfiguration(
            orgId, sourcingRuleEditRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Sourcing rules updated successfully")
            .payload(response)
            .build());
  }
}
