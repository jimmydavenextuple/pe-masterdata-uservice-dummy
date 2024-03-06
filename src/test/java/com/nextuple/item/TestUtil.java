/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item;

import com.nextuple.streams.promising.messages.PromisingRecord;
import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTime;

public class TestUtil {

  public static final String ORG_ID = "org-1";
  public static String ITEM_ID = "item-1";
  public static String UOM = "uom-1";

  private static final String SDND_ELIGIBLE = "sdndEligible";
  private static final String SDND_ELIGIBLE_FOR_DC = "sdndEligibleForDC";
  private static final String NEXT_DAY_ELIGIBLE = "nextdayEligible";
  private static final String NEXT_DAY_ELIGIBLE_FOR_DC = "nextdayEligibleForDC";
  private static final String EXPRESS_ELIGIBLE = "expressEligible";

  public PromisingRecord getItemRecord() {
    PromisingRecord record = new PromisingRecord();
    record.setItemId(TestUtil.ITEM_ID);
    record.setOrgId(TestUtil.ORG_ID);
    record.setUom(TestUtil.UOM);
    record.setLastModifiedDate(new DateTime());
    record.setColor("");
    record.setBopisEligible(Boolean.TRUE);
    record.setCost("");
    record.setDepartmentName("");
    record.setDepartmentNumber("");
    record.setIsDSVEligible(Boolean.TRUE);
    record.setExpressEligible(Boolean.TRUE);
    record.setIsWhiteGlove(Boolean.TRUE);
    record.setHeight(0.0);
    record.setParcelShipmentEligible(Boolean.TRUE);
    record.setBopisEligible(Boolean.TRUE);
    record.setShipAlone(Boolean.TRUE);
    record.setSdndEligible(Boolean.TRUE);
    record.setItemSource("");
    record.setShipEligible(Boolean.TRUE);
    record.setIsHazmat(Boolean.FALSE);
    record.setDimensionUom("");
    record.setLastModifiedDate(new DateTime());
    record.setWeight(0.0);
    record.setHeight(0.0);
    record.setLength(0.0);
    record.setWeightUOM("");
    record.setProduct("");
    record.setSize("");
    record.setVolumeUom("");
    record.setShortDescription("");
    record.setSdndEligibleForDC(Boolean.TRUE);
    record.setNextdayEligible(Boolean.TRUE);
    record.setNextdayEligibleForDC(Boolean.TRUE);
    return record;
  }

  public PromisingRecord getItemRecordItemEligibleStore() {
    PromisingRecord record = new PromisingRecord();
    record.setItemId(TestUtil.ITEM_ID);
    record.setOrgId(TestUtil.ORG_ID);
    record.setUom(TestUtil.UOM);
    record.setLastModifiedDate(new DateTime());
    record.setColor("");
    record.setBopisEligible(Boolean.TRUE);
    record.setCost("");
    record.setDepartmentName("");
    record.setDepartmentNumber("");
    record.setIsDSVEligible(Boolean.TRUE);
    record.setExpressEligible(Boolean.TRUE);
    record.setIsWhiteGlove(Boolean.TRUE);
    record.setHeight(0.0);
    record.setParcelShipmentEligible(Boolean.TRUE);
    record.setBopisEligible(Boolean.TRUE);
    record.setShipAlone(Boolean.TRUE);
    record.setItemSource("");
    record.setShipEligible(Boolean.TRUE);
    record.setIsHazmat(Boolean.FALSE);
    record.setDimensionUom("");
    record.setLastModifiedDate(new DateTime());
    record.setWeight(0.0);
    record.setHeight(0.0);
    record.setLength(0.0);
    record.setWeightUOM("");
    record.setProduct("");
    record.setSize("");
    record.setVolumeUom("");
    record.setShortDescription("");
    record.setSdndEligible(Boolean.TRUE);
    record.setSdndEligibleForDC(Boolean.FALSE);
    record.setNextdayEligible(Boolean.TRUE);
    record.setNextdayEligibleForDC(Boolean.FALSE);
    return record;
  }

  public PromisingRecord getItemRecordItemEligibleDC() {
    PromisingRecord record = new PromisingRecord();
    record.setItemId(TestUtil.ITEM_ID);
    record.setOrgId(TestUtil.ORG_ID);
    record.setUom(TestUtil.UOM);
    record.setLastModifiedDate(new DateTime());
    record.setColor("");
    record.setBopisEligible(Boolean.TRUE);
    record.setCost("");
    record.setDepartmentName("");
    record.setDepartmentNumber("");
    record.setIsDSVEligible(Boolean.TRUE);
    record.setExpressEligible(Boolean.TRUE);
    record.setIsWhiteGlove(Boolean.TRUE);
    record.setHeight(0.0);
    record.setParcelShipmentEligible(Boolean.TRUE);
    record.setBopisEligible(Boolean.TRUE);
    record.setShipAlone(Boolean.TRUE);
    record.setItemSource("");
    record.setShipEligible(Boolean.TRUE);
    record.setIsHazmat(Boolean.FALSE);
    record.setDimensionUom("");
    record.setLastModifiedDate(new DateTime());
    record.setWeight(0.0);
    record.setHeight(0.0);
    record.setLength(0.0);
    record.setWeightUOM("");
    record.setProduct("");
    record.setSize("");
    record.setVolumeUom("");
    record.setShortDescription("");
    record.setSdndEligible(Boolean.FALSE);
    record.setSdndEligibleForDC(Boolean.TRUE);
    record.setNextdayEligible(Boolean.FALSE);
    record.setNextdayEligibleForDC(Boolean.TRUE);
    return record;
  }

  public PromisingRecord getItemRecordItemEligibleNone() {
    PromisingRecord record = new PromisingRecord();
    record.setItemId(TestUtil.ITEM_ID);
    record.setOrgId(TestUtil.ORG_ID);
    record.setUom(TestUtil.UOM);
    record.setLastModifiedDate(new DateTime());
    record.setColor("");
    record.setBopisEligible(Boolean.TRUE);
    record.setCost("");
    record.setDepartmentName("");
    record.setDepartmentNumber("");
    record.setIsDSVEligible(Boolean.TRUE);
    record.setExpressEligible(Boolean.TRUE);
    record.setIsWhiteGlove(Boolean.TRUE);
    record.setHeight(0.0);
    record.setParcelShipmentEligible(Boolean.TRUE);
    record.setBopisEligible(Boolean.TRUE);
    record.setShipAlone(Boolean.TRUE);
    record.setItemSource("");
    record.setShipEligible(Boolean.TRUE);
    record.setIsHazmat(Boolean.FALSE);
    record.setDimensionUom("");
    record.setLastModifiedDate(new DateTime());
    record.setWeight(0.0);
    record.setHeight(0.0);
    record.setLength(0.0);
    record.setWeightUOM("");
    record.setProduct("");
    record.setSize("");
    record.setVolumeUom("");
    record.setShortDescription("");
    record.setSdndEligible(Boolean.FALSE);
    record.setSdndEligibleForDC(Boolean.FALSE);
    record.setNextdayEligible(Boolean.FALSE);
    record.setNextdayEligibleForDC(Boolean.FALSE);
    return record;
  }

  public static Map<String, Boolean> getServiceOptionEligibitiesMapForTest() {
    Map<String, Boolean> serviceOptionEligibilities = new HashMap<>();
    serviceOptionEligibilities.put(EXPRESS_ELIGIBLE, true);
    serviceOptionEligibilities.put(SDND_ELIGIBLE, true);
    serviceOptionEligibilities.put(SDND_ELIGIBLE_FOR_DC, true);
    serviceOptionEligibilities.put(NEXT_DAY_ELIGIBLE, true);
    serviceOptionEligibilities.put(NEXT_DAY_ELIGIBLE_FOR_DC, true);
    return serviceOptionEligibilities;
  }

  public static Map<String, Boolean> getServiceOptEligiblityMapForExceptionTest() {
    Map<String, Boolean> serviceOptionEligibilities = new HashMap<>();
    serviceOptionEligibilities.put(EXPRESS_ELIGIBLE, false);
    serviceOptionEligibilities.put(SDND_ELIGIBLE, false);
    serviceOptionEligibilities.put(SDND_ELIGIBLE_FOR_DC, false);
    serviceOptionEligibilities.put(NEXT_DAY_ELIGIBLE, false);
    serviceOptionEligibilities.put(NEXT_DAY_ELIGIBLE_FOR_DC, false);
    return serviceOptionEligibilities;
  }

  public Map<String, Object> getJsonProps() {
    Map<String, Object> props = new HashMap<>();
    props.put("key-deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(
        "value-deserializer",
        "org.springframework.kafka.support.serializer.ErrorHandlingDeserializer");
    props.put("bootstrap-servers", "localhost:90924");
    props.put("enable-auto-commit", "true");
    props.put("auto-offset-reset", "latest");
    Map<String, Object> properties = new HashMap<>();
    properties.put(
        "spring-deserializer-value-delegate-class",
        "org.springframework.kafka.support.serializer.JsonDeserializer");
    properties.put("spring-json-trusted-packages", "*");
    Map<String, Object> saslProps = new HashMap<>();
    saslProps.put("mechanism", "SCRAM-SHA-512");
    saslProps.put("jaas", Map.of("config", "exampleConfig"));
    properties.put("sasl", saslProps);
    properties.put("security", Map.of("protocol", "SASL_SSL"));
    props.put("properties", properties);

    return props;
  }
}
