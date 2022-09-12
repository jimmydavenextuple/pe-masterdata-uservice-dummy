package com.hbc.item.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemUtils {
  public static String SDNDELIGIBLE = "sdndEligible";
  public static String SDNDELIGIBLEFORDC = "sdndEligibleForDC";
  public static String NEXTDAYELIGIBLE = "nextDayEligible";
  public static String NEXTDAYELIGIBLEFORDC = "nextDayEligibleForDC";
  public static String SDND = "sdnd";
  public static String NEXTDAY = "nextday";
  public static String STORE = "Store";
  public static String FC = "FC";
  public static String MFC = "MFC";
  public static String EXPRESSELIGIBLE = "expressEligible";

  public static Map<String, List<String>> getInventoryNodeTypeMap(
      Map<String, Boolean> serviceOptionEligibilityMap) {
    Map<String, List<String>> inventoryNodeTypeMap = new HashMap<>();
    List<String> sdndEligibleNodeTypeList = new ArrayList<>();
    List<String> nextdayEligibleNodeTypeList = new ArrayList<>();
    if (serviceOptionEligibilityMap.containsKey(SDNDELIGIBLE)
        && Boolean.TRUE.equals(serviceOptionEligibilityMap.get(SDNDELIGIBLE))) {
      sdndEligibleNodeTypeList.add(STORE);
    }
    if (serviceOptionEligibilityMap.containsKey(SDNDELIGIBLEFORDC)
        && Boolean.TRUE.equals(serviceOptionEligibilityMap.get(SDNDELIGIBLEFORDC))) {
      sdndEligibleNodeTypeList.add(FC);
    }
    if (serviceOptionEligibilityMap.containsKey(NEXTDAYELIGIBLE)
        && Boolean.TRUE.equals(serviceOptionEligibilityMap.get(NEXTDAYELIGIBLE))) {
      nextdayEligibleNodeTypeList.add(STORE);
    }
    if (serviceOptionEligibilityMap.containsKey(NEXTDAYELIGIBLEFORDC)
        && Boolean.TRUE.equals(serviceOptionEligibilityMap.get(NEXTDAYELIGIBLEFORDC))) {
      nextdayEligibleNodeTypeList.add(FC);
    }
    inventoryNodeTypeMap.put(SDND, sdndEligibleNodeTypeList);
    inventoryNodeTypeMap.put(NEXTDAY, nextdayEligibleNodeTypeList);
    return inventoryNodeTypeMap;
  }

  public static Map<String, Boolean> getServiceOptionEligibitiesMapForTest() {
    Map<String, Boolean> serviceOptionEligibilities = new HashMap<>();
    serviceOptionEligibilities.put(EXPRESSELIGIBLE, true);
    serviceOptionEligibilities.put(SDNDELIGIBLE, true);
    serviceOptionEligibilities.put(SDNDELIGIBLEFORDC, true);
    serviceOptionEligibilities.put(NEXTDAYELIGIBLE, true);
    serviceOptionEligibilities.put(NEXTDAYELIGIBLEFORDC, false);
    return serviceOptionEligibilities;
  }
}
