package com.nextuple.item.consumer;

import com.nextuple.item.TestUtil;
import com.nextuple.item.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ItemMasterConsumerTest {
  @InjectMocks private ItemMasterConsumer itemMasterConsumer;

  @InjectMocks private TestUtil testUtil;

  @Mock private ItemService itemService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  //  @Test
  //  void createCarrierServiceTest() throws ItemDomainException {
  //    when(itemService.createItem(any())).thenReturn(testUtil.getItemResponse());
  //    ArgumentCaptor<ItemCreationRequest> captor =
  // ArgumentCaptor.forClass(ItemCreationRequest.class);
  //    itemMasterConsumer.onItemMasterDataConsumption(testUtil.getItemMasterEvent(), null);
  //
  //    verify(itemService, times(1)).createItem(captor.capture());
  //    Assertions.assertEquals(TestUtil.ITEM_ID, captor.getValue().getItemId());
  //    Assertions.assertEquals(2, captor.getValue().getServiceOptionEligibilities().size());
  //  }
}
