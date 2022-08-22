package com.hbc.item.domain.feign;

import com.hbc.common.response.BaseResponse;
import com.hbc.item.domain.inbound.ItemCreationRequest;
import com.hbc.item.domain.inbound.ItemUpdationRequest;
import com.hbc.item.domain.outbound.ItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
    name = "pe-config-item",
    url = "${spring.application.dependencies.item:http://pe-config-item:8080/}")
public interface ItemFeign {

  @PostMapping("/item")
  BaseResponse<ItemResponse> addItem(@RequestBody ItemCreationRequest itemCreationRequest);

  @PutMapping("/item/{itemId}/{orgId}/{uom}")
  BaseResponse<ItemResponse> updateItemDetails(
      @PathVariable(name = "itemId") String itemId,
      @PathVariable(name = "orgId") String orgId,
      @PathVariable(name = "uom") String uom,
      @RequestBody ItemUpdationRequest itemUpdationRequest);

  @GetMapping("/item/{itemId}/{orgId}/{uom}")
  BaseResponse<ItemResponse> getItemDetails(
      @PathVariable(name = "itemId") String itemId,
      @PathVariable(name = "orgId") String orgId,
      @PathVariable(name = "uom") String uom);

  @DeleteMapping("/item/{itemId}/{orgId}/{uom}")
  BaseResponse<ItemResponse> deleteItem(
      @PathVariable(name = "itemId") String itemId,
      @PathVariable(name = "orgId") String orgId,
      @PathVariable(name = "uom") String uom);

  @GetMapping("/item/{orgId}/{uom}")
  List<ItemResponse> getItemDetailsList(
          @PathVariable(name = "orgId") String orgId,
          @PathVariable(name = "uom") String uom,
          @RequestParam List<String> itemList);
}
