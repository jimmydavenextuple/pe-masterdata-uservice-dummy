package com.nextuple.dataupload.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.service.ItemDetailsService;
import com.nextuple.dataupload.util.TestUtil;
import com.nextuple.item.domain.outbound.ItemListResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ItemDashboardControllerTest {

  @InjectMocks ItemDashboardController itemDashboardController;

  @Mock ItemDetailsService itemDetailsService;

  @InjectMocks private TestUtil testUtil;

  @Test
  @DisplayName("Get item details list with pagination")
  void getItemDetails() {
    List<ItemListResponse> itemListResponses = testUtil.getItemList();

    when(itemDetailsService.getItemDetailsListpaginated(any(), any(), any()))
        .thenReturn(
            testUtil.setPagePayload(
                testUtil.getItemListPage(4, itemListResponses, itemListResponses.size())));
    ResponseEntity<BaseResponse<PagePayload<ItemListResponse>>> response =
        itemDashboardController.getItemDetails(
            TestUtil.ORG_ID,
            "item-01,item-02",
            testUtil.getPageParams(
                Optional.of(2), Optional.of(1), Optional.of("itemId"), Optional.of("ASC")));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        4,
        (int) response.getBody().getPayload().getPagination().getTotalPages(),
        "Pagination Total pages");
    Assertions.assertEquals(
        itemListResponses.size(),
        (int) response.getBody().getPayload().getPagination().getTotalRecords(),
        "Total Elements");
    Assertions.assertEquals(
        itemListResponses.size(),
        response.getBody().getPayload().getData().size(),
        "Paginated data");
    verify(itemDetailsService, times(1)).getItemDetailsListpaginated(any(), any(), any());
  }
}
