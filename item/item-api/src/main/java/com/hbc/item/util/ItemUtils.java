package com.hbc.item.util;

import java.util.*;

public class ItemUtils {
  private ItemUtils() {}

  private static final String SDND_ELIGIBLE = "sdndEligible";
  private static final String SDND_ELIGIBLE_FOR_DC = "sdndEligibleForDC";
  private static final String NEXT_DAY_ELIGIBLE = "nextdayEligible";
  private static final String NEXT_DAY_ELIGIBLE_FOR_DC = "nextdayEligibleForDC";
  private static final String SDND = "sdnd";
  private static final String NEXT_DAY = "nextday";
  private static final String STORE = "Store";
  private static final String FC = "FC";
  private static final String MFC = "MFC";

  public static Map<String, List<String>> getInventoryNodeTypeMap(
      Map<String, Boolean> serviceOptionEligibilityMap) {
    Map<String, List<String>> inventoryNodeTypeMap = new HashMap<>();
    List<String> sdndEligibleNodeTypeList = new ArrayList<>();
    List<String> nextdayEligibleNodeTypeList = new ArrayList<>();
    if (Objects.equals(Boolean.TRUE, serviceOptionEligibilityMap.get(SDND_ELIGIBLE))) {
      sdndEligibleNodeTypeList.add(STORE);
    }
    if (Objects.equals(Boolean.TRUE, serviceOptionEligibilityMap.get(SDND_ELIGIBLE_FOR_DC))) {
      sdndEligibleNodeTypeList.add(FC);
      sdndEligibleNodeTypeList.add(MFC);
    }
    if (Objects.equals(Boolean.TRUE, serviceOptionEligibilityMap.get(NEXT_DAY_ELIGIBLE))) {
      nextdayEligibleNodeTypeList.add(STORE);
    }
    if (Objects.equals(Boolean.TRUE, serviceOptionEligibilityMap.get(NEXT_DAY_ELIGIBLE_FOR_DC))) {
      nextdayEligibleNodeTypeList.add(FC);
      nextdayEligibleNodeTypeList.add(MFC);
    }
    inventoryNodeTypeMap.put(SDND, sdndEligibleNodeTypeList);
    inventoryNodeTypeMap.put(NEXT_DAY, nextdayEligibleNodeTypeList);
    return inventoryNodeTypeMap;
  }
}
