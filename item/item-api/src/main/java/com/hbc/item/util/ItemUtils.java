package com.hbc.item.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemUtils {
  public static Map<String, List<String>> getInventoryNodeTypeMap(
      Map<String, Boolean> serviceOptionEligibilityMap) {
    Map<String, List<String>> inventoryNodeTypeMap = new HashMap<String, List<String>>();
    List<String> sdndEligibleNodeTypeList = new ArrayList<String>();
    List<String> nextdayEligibleNodeTypeList = new ArrayList<String>();
    if (serviceOptionEligibilityMap.containsKey("sdndEligible")
        && serviceOptionEligibilityMap.get("sdndEligible")) {
      sdndEligibleNodeTypeList.add("Store");
    }
    if (serviceOptionEligibilityMap.containsKey("sdndEligibleForDC")
        && serviceOptionEligibilityMap.get("sdndEligibleForDC")) {
      sdndEligibleNodeTypeList.add("DC");
    }
    if (serviceOptionEligibilityMap.containsKey("nextDayEligible")
        && serviceOptionEligibilityMap.get("nextDayEligible")) {
      nextdayEligibleNodeTypeList.add("Store");
    }
    if (serviceOptionEligibilityMap.containsKey("nextDayEligibleForDC")
        && serviceOptionEligibilityMap.get("nextDayEligibleForDC")) {
      nextdayEligibleNodeTypeList.add("DC");
    }
    inventoryNodeTypeMap.put("sdnd", sdndEligibleNodeTypeList);
    inventoryNodeTypeMap.put("nextday", nextdayEligibleNodeTypeList);
    return inventoryNodeTypeMap;
  }

  public static Map<String, Boolean> getServiceOptionEligibitiesMapForTest() {
    Map<String, Boolean> serviceOptionEligibilities = new HashMap<>();
    serviceOptionEligibilities.put("expressEligible", true);
    serviceOptionEligibilities.put("sdndEligible", true);
    serviceOptionEligibilities.put("sdndEligibleForDC", true);
    serviceOptionEligibilities.put("nextDayEligible", true);
    serviceOptionEligibilities.put("nextDayEligibleForDC", false);
    return serviceOptionEligibilities;
  }
}
