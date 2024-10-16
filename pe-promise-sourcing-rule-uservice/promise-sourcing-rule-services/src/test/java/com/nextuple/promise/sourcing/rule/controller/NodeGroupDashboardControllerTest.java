package com.nextuple.promise.sourcing.rule.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.enums.ApplicationLayer;
import com.nextuple.common.enums.ExceptionCodeMapping;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.pojo.PageParams;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.TestUtil;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.DeleteNodeGroupsRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.UpdateSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupDetailWithPriorityResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.NodeGroupWithNodesResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.edit.sourcing.rules.UpdateSourcingRuleConfigurationResponse;
import com.nextuple.promise.sourcing.rule.service.NodeGroupService;
import com.nextuple.promise.sourcing.rule.service.SourcingRulesConfigurationService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class NodeGroupDashboardControllerTest {
  @Mock private NodeGroupService nodeGroupService;
  @Mock private SourcingRulesConfigurationService sourcingRulesConfigurationService;
  @InjectMocks NodeGroupDashboardController controller;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createNodeGroupTest() throws PromiseEngineException, CommonServiceException {
    NodeGroupWithNodesResponse response = testUtil.getNodeGroupWithNodesResponse();
    when(nodeGroupService.createNodeGroupFromDashboard(any(), any())).thenReturn(response);

    ResponseEntity<BaseResponse<NodeGroupWithNodesResponse>> responseEntity =
        controller.createNodeGroup("NEXTUPLE", response);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        response.getNodeGroupName(), responseEntity.getBody().getPayload().getNodeGroupName());

    verify(nodeGroupService, times(1)).createNodeGroupFromDashboard(any(), any());
  }

  @Test
  void createNodeGroupExceptionTest() throws PromiseEngineException, CommonServiceException {
    NodeGroupWithNodesResponse response = testUtil.getNodeGroupWithNodesResponse();
    when(nodeGroupService.createNodeGroupFromDashboard(any(), any()))
        .thenThrow(
            new PromiseEngineException(
                ApplicationLayer.CONTROLLER_LAYER, ExceptionCodeMapping.DAO_NOT_FOUND, null));

    PromiseEngineException exception =
        Assertions.assertThrows(
            PromiseEngineException.class, () -> controller.createNodeGroup("NEXTUPLE", response));
    Assertions.assertEquals(ApplicationLayer.CONTROLLER_LAYER, exception.getApplicationLayer());
    verify(nodeGroupService, times(1)).createNodeGroupFromDashboard(any(), any());
  }

  @Test
  void editNodeGroupTest() throws PromiseEngineException, CommonServiceException {
    NodeGroupWithNodesResponse response = testUtil.getNodeGroupWithNodesResponse();
    when(nodeGroupService.editNodeGroupFromDashboard(any(), any(), any())).thenReturn(response);

    ResponseEntity<BaseResponse<NodeGroupWithNodesResponse>> responseEntity =
        controller.editNodeGroup("NEXTUPLE", 42L, response);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        response.getNodeGroupName(), responseEntity.getBody().getPayload().getNodeGroupName());

    verify(nodeGroupService, times(1)).editNodeGroupFromDashboard(any(), any(), any());
  }

  @Test
  void editNodeGroupExceptionTest() throws CommonServiceException, PromiseEngineException {
    NodeGroupWithNodesResponse response = testUtil.getNodeGroupWithNodesResponse();
    when(nodeGroupService.editNodeGroupFromDashboard(any(), any(), any()))
        .thenThrow(
            new PromiseEngineException(
                ApplicationLayer.CONTROLLER_LAYER, ExceptionCodeMapping.DAO_NOT_FOUND, null));

    PromiseEngineException exception =
        Assertions.assertThrows(
            PromiseEngineException.class,
            () -> controller.editNodeGroup("NEXTUPLE", 42L, response));
    Assertions.assertEquals(ApplicationLayer.CONTROLLER_LAYER, exception.getApplicationLayer());
    verify(nodeGroupService, times(1)).editNodeGroupFromDashboard(any(), any(), any());
  }

  @Test
  void editNodeGroupValidationTest() throws PromiseEngineException, CommonServiceException {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    NodeGroupWithNodesResponse response = testUtil.getNodeGroupWithNodesResponse();
    response.getNodes().get(0).setNodeId(" ");
    Set<ConstraintViolation<NodeGroupWithNodesResponse>> violationSet =
        validator.validate(response);
    Assertions.assertFalse(violationSet.isEmpty());

    response.getNodes().get(0).setSequence(null);
    violationSet = validator.validate(response);
    Assertions.assertFalse(violationSet.isEmpty());

    response.setNodes(new ArrayList<>());
    violationSet = validator.validate(response);
    Assertions.assertFalse(violationSet.isEmpty());
  }

  @Test
  void deleteNodeGroupTest() throws PromiseEngineException, CommonServiceException {
    NodeGroupWithNodesResponse response = testUtil.getNodeGroupWithNodesResponse();
    when(nodeGroupService.deleteNodeGroupAndAssociatedNodePriority(any(), any()))
        .thenReturn(response);

    ResponseEntity<BaseResponse<NodeGroupWithNodesResponse>> responseEntity =
        controller.deleteNodeGroup("NEXTUPLE", 42L);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        response.getNodeGroupName(), responseEntity.getBody().getPayload().getNodeGroupName());

    verify(nodeGroupService, times(1)).deleteNodeGroupAndAssociatedNodePriority(any(), any());
  }

  @Test
  void deleteNodeGroupExceptionTest() throws PromiseEngineException, CommonServiceException {
    NodeGroupWithNodesResponse response = testUtil.getNodeGroupWithNodesResponse();
    when(nodeGroupService.deleteNodeGroupAndAssociatedNodePriority(any(), any()))
        .thenThrow(
            new PromiseEngineException(
                ApplicationLayer.CONTROLLER_LAYER, ExceptionCodeMapping.DAO_NOT_FOUND, null));

    PromiseEngineException exception =
        Assertions.assertThrows(
            PromiseEngineException.class, () -> controller.deleteNodeGroup("NEXTUPLE", 42L));
    Assertions.assertEquals(ApplicationLayer.CONTROLLER_LAYER, exception.getApplicationLayer());
    verify(nodeGroupService, times(1)).deleteNodeGroupAndAssociatedNodePriority(any(), any());
  }

  @Test
  void deleteMultiNodeGroupTest() throws PromiseEngineException, CommonServiceException {
    DeleteNodeGroupsRequest request = testUtil.getDeleteNodeGroupsRequest();
    doNothing()
        .when(nodeGroupService)
        .deleteNodeGroupsAndAssociatedNodePriorityByOrgId(any(), any());

    ResponseEntity<BaseResponse<String>> responseEntity =
        controller.deleteNodeGroupsByOrgId("NEXTUPLE", request);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    verify(nodeGroupService, times(1))
        .deleteNodeGroupsAndAssociatedNodePriorityByOrgId(any(), any());
  }

  @Test
  void deleteMultiNodeGroupExceptionTest() throws PromiseEngineException, CommonServiceException {
    DeleteNodeGroupsRequest request = testUtil.getDeleteNodeGroupsRequest();
    doThrow(
            new PromiseEngineException(
                ApplicationLayer.CONTROLLER_LAYER, ExceptionCodeMapping.DAO_NOT_FOUND, null))
        .when(nodeGroupService)
        .deleteNodeGroupsAndAssociatedNodePriorityByOrgId(any(), any());
    PromiseEngineException exception =
        Assertions.assertThrows(
            PromiseEngineException.class,
            () -> controller.deleteNodeGroupsByOrgId("NEXTUPLE", request));
    Assertions.assertEquals(ApplicationLayer.CONTROLLER_LAYER, exception.getApplicationLayer());
    verify(nodeGroupService, times(1))
        .deleteNodeGroupsAndAssociatedNodePriorityByOrgId(any(), any());
  }

  @Test
  void getNodeGroupWithPriorityExceptionTest()
      throws PromiseEngineException, CommonServiceException {
    when(nodeGroupService.fetchNodeGroupsByOrgIdWithNodePriority(any(), any()))
        .thenThrow(
            new PromiseEngineException(
                ApplicationLayer.CONTROLLER_LAYER, ExceptionCodeMapping.DAO_NOT_FOUND, null));

    PageParams pageParams =
        testUtil.getPageParams(
            Optional.of(2),
            Optional.of(1),
            Optional.of(TestUtil.SORT_BY),
            Optional.of(TestUtil.DEFAULT_SORT_ORDER));

    PromiseEngineException exception =
        Assertions.assertThrows(
            PromiseEngineException.class,
            () -> controller.getNodeGroupWithPriority("NEXTUPLE", pageParams));
    Assertions.assertEquals(ApplicationLayer.CONTROLLER_LAYER, exception.getApplicationLayer());
    verify(nodeGroupService, times(1)).fetchNodeGroupsByOrgIdWithNodePriority(any(), any());
  }

  @Test
  void getNodeGroupWithPriorityTest() throws PromiseEngineException, CommonServiceException {
    PageResponse<NodeGroupDetailWithPriorityResponse> pageResponse =
        testUtil.getNodeGroupDetailsWithPriorityResponse();
    when(nodeGroupService.fetchNodeGroupsByOrgIdWithNodePriority(any(), any()))
        .thenReturn(pageResponse);

    PageParams pageParams =
        testUtil.getPageParams(
            Optional.of(2),
            Optional.of(1),
            Optional.of(TestUtil.SORT_BY),
            Optional.of(TestUtil.DEFAULT_SORT_ORDER));

    ResponseEntity<BaseResponse<PageResponse<NodeGroupDetailWithPriorityResponse>>> responseEntity =
        controller.getNodeGroupWithPriority("NEXTUPLE", pageParams);

    Assertions.assertEquals(
        pageResponse.getData().size(), responseEntity.getBody().getPayload().getData().size());
    Assertions.assertEquals(2, pageResponse.getPagination().getTotalPages());
    Assertions.assertEquals(1, pageResponse.getPagination().getTotalRecords());
    Assertions.assertEquals(1, pageResponse.getPagination().getCurrentPage());
    Assertions.assertEquals("id", pageResponse.getPagination().getSortBy());
    verify(nodeGroupService, times(1)).fetchNodeGroupsByOrgIdWithNodePriority(any(), any());
  }

  @Test
  void editSourcingRuleTest() throws PromiseEngineException, CommonServiceException {
    UpdateSourcingRuleRequest request = testUtil.getUpdateSourcingRuleRequest();
    UpdateSourcingRuleConfigurationResponse updateSourcingRuleConfigurationResponse =
        testUtil.getUpdateSourcingRuleConfigurationResponse();
    when(sourcingRulesConfigurationService.editSourcingRuleConfiguration(any(), any()))
        .thenReturn(updateSourcingRuleConfigurationResponse);
    ResponseEntity<BaseResponse<UpdateSourcingRuleConfigurationResponse>> responseEntity =
        controller.editSourcingRuleConfiguration("ABC", request);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(2, responseEntity.getBody().getPayload().getNodes().size());
    assertTrue(responseEntity.getBody().getPayload().getRequiredAttributes().size() > 0);
    assertTrue(responseEntity.getBody().getPayload().getOptionalAttributes().size() > 0);
    verify(sourcingRulesConfigurationService, times(1)).editSourcingRuleConfiguration(any(), any());
  }

  @Test
  void editSourcingRuleExceptionTest() throws PromiseEngineException, CommonServiceException {
    when(sourcingRulesConfigurationService.editSourcingRuleConfiguration(any(), any()))
        .thenThrow(
            new PromiseEngineException(
                ApplicationLayer.SERVICE_LAYER,
                ExceptionCodeMapping.DAO_FIND_FAILED,
                "Unable to find sourcing rule."));

    PromiseEngineException exception =
        Assertions.assertThrows(
            PromiseEngineException.class,
            () -> controller.editSourcingRuleConfiguration(any(), any()));
    Assertions.assertEquals(ApplicationLayer.SERVICE_LAYER, exception.getApplicationLayer());
    verify(sourcingRulesConfigurationService, times(1)).editSourcingRuleConfiguration(any(), any());
  }
}
