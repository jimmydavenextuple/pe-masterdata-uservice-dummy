/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.item.TestUtil;
import com.nextuple.item.domain.inbound.DeleteItemSubstitutionRequest;
import com.nextuple.item.domain.inbound.UpsertItemSubstitutionRequest;
import com.nextuple.item.domain.outbound.ItemSubstitutionResponse;
import com.nextuple.item.persistence.domain.ItemSubstitutionDomainDto;
import com.nextuple.item.persistence.service.ItemSubstitutionPersistenceService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ItemSubstitutionServiceTest {

  @Mock private ItemSubstitutionPersistenceService itemSubstitutionPersistenceService;

  @InjectMocks private ItemSubstitutionService itemSubstitutionService;
  @InjectMocks private TestUtil testUtil;

  private UpsertItemSubstitutionRequest upsertRequest;
  private DeleteItemSubstitutionRequest deleteRequest;
  private ItemSubstitutionDomainDto mockDomainDto;

  @BeforeEach
  void setUp() {
    // Setup test data
    upsertRequest = testUtil.getUpsertItemSubstitutionRequest();
    deleteRequest = testUtil.getDeleteItemSubstitutionRequest();
    mockDomainDto = testUtil.getItemSubstitutionDomainDto();
  }

  // OCDeLine: Testing upsertItemSubstitution when priority is not provided
  @Test
  @DisplayName(
      "Test upsert item substitution with null priority - Should set default priority to 1")
  void testUpsertItemSubstitutionWithNullPriority() {
    // Arrange
    upsertRequest.setPriority(null); // Setting priority to null to test the default behavior

    when(itemSubstitutionPersistenceService.save(any(ItemSubstitutionDomainDto.class)))
        .thenReturn(mockDomainDto);

    // Act
    ItemSubstitutionResponse response =
        itemSubstitutionService.upsertItemSubstitution(upsertRequest);

    // Assert
    assertEquals(1, upsertRequest.getPriority()); // Verify priority was set to default value 1
    assertNotNull(response);
    verify(itemSubstitutionPersistenceService, times(1)).save(any(ItemSubstitutionDomainDto.class));
  }

  @Test
  @DisplayName(
      "Test upsert item substitution with provided priority - Should keep provided priority")
  void testUpsertItemSubstitutionWithProvidedPriority() {
    // Arrange
    upsertRequest.setPriority(5); // Explicitly setting priority to test non-default behavior

    when(itemSubstitutionPersistenceService.save(any(ItemSubstitutionDomainDto.class)))
        .thenReturn(mockDomainDto);

    // Act
    ItemSubstitutionResponse response =
        itemSubstitutionService.upsertItemSubstitution(upsertRequest);

    // Assert
    assertEquals(5, upsertRequest.getPriority()); // Verify priority remains as provided
    assertNotNull(response);
    verify(itemSubstitutionPersistenceService, times(1)).save(any(ItemSubstitutionDomainDto.class));
  }

  @Test
  @DisplayName("Test upsert item substitution mapping - Should correctly map request to domain DTO")
  void testUpsertItemSubstitutionMapping() {
    // Arrange
    upsertRequest.setPriority(3);
    mockDomainDto.setPriority(3);

    when(itemSubstitutionPersistenceService.save(any(ItemSubstitutionDomainDto.class)))
        .thenReturn(mockDomainDto);

    // Act
    ItemSubstitutionResponse response =
        itemSubstitutionService.upsertItemSubstitution(upsertRequest);

    // Assert
    assertNotNull(response);
    // Verify the persistence service was called with a mapped domain DTO
    verify(itemSubstitutionPersistenceService, times(1)).save(any(ItemSubstitutionDomainDto.class));
  }

  @Test
  @DisplayName(
      "Test get item substitution by orgId, primaryItemId and primaryUom - Should return list of substitutions when found")
  void testGetItemSubstitutionByOrgIdAndPrimaryItemIdAndPrimaryUom() {
    // Arrange
    List<ItemSubstitutionDomainDto> mockResults = new ArrayList<>();
    mockResults.add(mockDomainDto);

    when(itemSubstitutionPersistenceService.findByOrgIdAndPrimaryItemIdAndPrimaryUom(
            "test-org", "item1", "EACH"))
        .thenReturn(mockResults);

    // Act
    List<ItemSubstitutionResponse> responses =
        itemSubstitutionService.getItemSubstitutionByOrgIdAndPrimaryItemIdAndPrimaryUom(
            "test-org", "item1", "EACH");

    // Assert
    assertNotNull(responses);
    assertFalse(responses.isEmpty());
    assertEquals(1, responses.size());
    verify(itemSubstitutionPersistenceService, times(1))
        .findByOrgIdAndPrimaryItemIdAndPrimaryUom("test-org", "item1", "EACH");
  }

  @Test
  @DisplayName(
      "Test get item substitution by orgId, primaryItemId and primaryUom with no results - Should return empty list")
  void testGetItemSubstitutionByOrgIdAndPrimaryItemIdAndPrimaryUomNoResults() {
    // Arrange
    when(itemSubstitutionPersistenceService.findByOrgIdAndPrimaryItemIdAndPrimaryUom(
            "test-org", "item1", "EACH"))
        .thenReturn(new ArrayList<>());

    // Act
    List<ItemSubstitutionResponse> responses =
        itemSubstitutionService.getItemSubstitutionByOrgIdAndPrimaryItemIdAndPrimaryUom(
            "test-org", "item1", "EACH");

    // Assert
    assertNotNull(responses);
    assertTrue(responses.isEmpty());
    verify(itemSubstitutionPersistenceService, times(1))
        .findByOrgIdAndPrimaryItemIdAndPrimaryUom("test-org", "item1", "EACH");
  }

  @Test
  @DisplayName("Test delete item substitution - Should delete item when found")
  void testDeleteItemSubstitution() {
    // Arrange
    when(itemSubstitutionPersistenceService
            .findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
                deleteRequest.getOrgId(),
                deleteRequest.getPrimaryItemId(),
                deleteRequest.getPrimaryUom(),
                deleteRequest.getAlternateItemId(),
                deleteRequest.getAlternateUom()))
        .thenReturn(Optional.of(mockDomainDto));

    // Act
    itemSubstitutionService.deleteItemSubstitution(deleteRequest);

    // Assert
    verify(itemSubstitutionPersistenceService, times(1)).delete(mockDomainDto);
  }

  @Test
  @DisplayName("Test delete item substitution not found - Should throw ResponseStatusException")
  void testDeleteItemSubstitutionNotFound() {
    // Arrange
    when(itemSubstitutionPersistenceService
            .findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
                any(), any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    // Act & Assert
    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () -> itemSubstitutionService.deleteItemSubstitution(deleteRequest));

    // Verify exception details
    assertEquals("404 NOT_FOUND", exception.getStatusCode().toString());
    assertTrue(exception.getReason().contains("Item substitution not found"));

    // Verify delete was never called
    verify(itemSubstitutionPersistenceService, never()).delete(any());
  }

  @Test
  @DisplayName("Test delete item substitution with different item IDs - Should find correct item")
  void testDeleteItemSubstitutionWithDifferentIds() {
    // Arrange
    deleteRequest.setAlternateItemId("item3"); // Different from the setUp

    ItemSubstitutionDomainDto differentMockDto = testUtil.getItemSubstitutionDomainDto();
    differentMockDto.setAlternateItemId("item3");

    when(itemSubstitutionPersistenceService
            .findByOrgIdAndPrimaryItemIdAndPrimaryUomAndAlternateItemIdAndAlternateUom(
                deleteRequest.getOrgId(),
                deleteRequest.getPrimaryItemId(),
                deleteRequest.getPrimaryUom(),
                deleteRequest.getAlternateItemId(),
                deleteRequest.getAlternateUom()))
        .thenReturn(Optional.of(differentMockDto));

    // Act
    itemSubstitutionService.deleteItemSubstitution(deleteRequest);

    // Assert
    verify(itemSubstitutionPersistenceService, times(1)).delete(differentMockDto);
  }
}
