package com.nextuple.node;

import com.nextuple.node.domain.entity.NodeEntity;
import com.nextuple.node.domain.inbound.NodeRequest;
import com.nextuple.node.domain.inbound.NodeUpdationRequest;
import com.nextuple.node.domain.outbound.NodeResponse;

public class TestUtil {

  public static final String NODE_ID = "node-1";
  public static final String ORG_ID = "org-1";
  public static final String STREET = "street-1";
  public static final String CITY = "city-1";
  public static final String PROVINCE = "province-1";
  public static final String POSTAL_CODE = "33666";
  public static final String COUNTRY = "country-1";
  public static final String LATITUDE = "43.769912";
  public static final String LONGITUDE = "-79.296678";
  public static final String TIME_ZONE = "America/Toronto";
  public static Boolean SHIP_TO_TIME = Boolean.TRUE;
  public static Boolean BOPIS_ELIGIBLE = Boolean.TRUE;
  public static Boolean SDND_ELIGIBLE = Boolean.TRUE;
  public static Boolean EXPRESS_ELIGIBLE = Boolean.TRUE;
  public static String NODE_TYPE = "MFC";
  public static Boolean IS_ACTIVE = Boolean.TRUE;

  public NodeEntity getNodeEntity() {
    return NodeEntity.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .country(COUNTRY)
        .expressEligible(EXPRESS_ELIGIBLE)
        .nodeType(NODE_TYPE)
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .sdndEligible(SDND_ELIGIBLE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .build();
  }

  public NodeRequest getNodeRequest() {
    return NodeRequest.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .country(COUNTRY)
        .expressEligible(EXPRESS_ELIGIBLE)
        .nodeType(NODE_TYPE)
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .sdndEligible(SDND_ELIGIBLE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .build();
  }

  public NodeResponse getNodeResponse() {
    return NodeResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city(CITY)
        .country(COUNTRY)
        .expressEligible(EXPRESS_ELIGIBLE)
        .nodeType(NODE_TYPE)
        .isActive(IS_ACTIVE)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .sdndEligible(SDND_ELIGIBLE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .build();
  }

  public NodeUpdationRequest getNodeUpdationRequest() {
    return NodeUpdationRequest.builder()
        .isActive(Boolean.FALSE)
        .city("city-2")
        .latitude("3526.5262")
        .build();
  }

  public NodeResponse getUpdatedNodeResponse() {
    return NodeResponse.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city("city-2")
        .country(COUNTRY)
        .expressEligible(EXPRESS_ELIGIBLE)
        .nodeType(NODE_TYPE)
        .isActive(Boolean.FALSE)
        .latitude("3526.5262")
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .sdndEligible(SDND_ELIGIBLE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .build();
  }

  public NodeEntity getUpdatedNodeEntity() {
    return NodeEntity.builder()
        .nodeId(NODE_ID)
        .orgId(ORG_ID)
        .street(STREET)
        .bopisEligible(BOPIS_ELIGIBLE)
        .city("city-2")
        .country(COUNTRY)
        .expressEligible(EXPRESS_ELIGIBLE)
        .nodeType(NODE_TYPE)
        .isActive(Boolean.FALSE)
        .latitude("3526.5262")
        .longitude(LONGITUDE)
        .postalCode(POSTAL_CODE)
        .sdndEligible(SDND_ELIGIBLE)
        .province(PROVINCE)
        .shipToHome(SHIP_TO_TIME)
        .timezone(TIME_ZONE)
        .build();
  }
}
