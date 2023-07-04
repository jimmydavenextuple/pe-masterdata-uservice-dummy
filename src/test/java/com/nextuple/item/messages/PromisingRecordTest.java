/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.messages;

import com.nextuple.messages.PromisingRecord;
import com.nextuple.messages.PromisingRecord.Builder;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

@PrepareForTest(PromisingRecord.class)
class PromisingRecordTest {

  @Test
  @DisplayName("Testing for Builder when all fields have values")
  void BuilderTest() {
    Builder builder = PromisingRecord.newBuilder(getPromiseRecord());
    Builder result = PromisingRecord.newBuilder(builder);

    Assertions.assertEquals(builder.getBopisEligible(), result.getBopisEligible());
    Assertions.assertEquals(builder.getColor(), result.getColor());
    Assertions.assertEquals(builder.getCost(), result.getCost());
    Assertions.assertEquals(builder.getHeight(), result.getHeight());
    Assertions.assertEquals(builder.getDepartmentName(), result.getDepartmentName());
  }

  @Test
  @DisplayName("Testing for PromisingRecord when fields have values")
  void promiseRecordTest() {

    PromisingRecord promisingRecord = getPromiseRecord();
    Builder result = PromisingRecord.newBuilder(promisingRecord);

    Assertions.assertEquals(promisingRecord.getBopisEligible(), result.getBopisEligible());
    Assertions.assertEquals(promisingRecord.getColor(), result.getColor());
    Assertions.assertEquals(promisingRecord.getCost(), result.getCost());
    Assertions.assertEquals(promisingRecord.getHeight(), result.getHeight());
    Assertions.assertEquals(promisingRecord.getDepartmentName(), result.getDepartmentName());
    Assertions.assertEquals(promisingRecord.getDimensionUom(), result.getDimensionUom());
    Assertions.assertEquals(promisingRecord.getExpressEligible(), result.getExpressEligible());
  }

  @Test
  @DisplayName("Testing for PromisingRecord/Builder when schema is different")
  void promiseRecordForNullValuesTest() {
    String schema =
        "{\"type\":\"record\",\"name\":\"PromisingRecord\",\"namespace\":\"com.nextuple.streams.promising.messages\",\"doc\":\"SKU record to be published to Promising Engine destination topic\",\"fields\":[{\"name\":\"itemId\",\"type\":\"string\",\"doc\":\"Sku ID\",\"default\":\"null\"},{\"name\":\"itemSource\",\"type\":\"string\",\"default\":\"null\"},{\"name\":\"orgId\",\"type\":\"string\",\"default\":\"null\"},{\"name\":\"uom\",\"type\":\"string\",\"default\":\"null\"},{\"name\":\"isDSVEligible\",\"type\":\"boolean\",\"default\":null},{\"name\":\"product\",\"type\":\"string\",\"default\":null},{\"name\":\"color\",\"type\":\"string\",\"default\":null},{\"name\":\"size\",\"type\":\"string\",\"default\":null},{\"name\":\"shipAlone\",\"type\":\"boolean\",\"default\":null},{\"name\":\"shipEligible\",\"type\":\"boolean\"},{\"name\":\"parcelShipmentEligible\",\"type\":\"boolean\"},{\"name\":\"bopisEligible\",\"type\":\"boolean\"},{\"name\":\"expressEligible\",\"type\":\"boolean\"},{\"name\":\"sdndEligible\",\"type\":\"boolean\"},{\"name\":\"isWhiteGlove\",\"type\":\"boolean\"},{\"name\":\"height\",\"type\":\"double\",\"default\":null},{\"name\":\"width\",\"type\":\"double\",\"default\":null},{\"name\":\"length\",\"type\":\"double\",\"default\":null},{\"name\":\"volume\",\"type\":\"double\",\"default\":null},{\"name\":\"dimensionUom\",\"type\":\"string\",\"default\":\"IN\"},{\"name\":\"volumeUom\",\"type\":\"string\",\"default\":\"CU IN\"},{\"name\":\"weight\",\"type\":\"double\",\"default\":null},{\"name\":\"weightUOM\",\"type\":\"string\",\"default\":\"LB\"},{\"name\":\"processingTime\",\"type\":\"long\",\"default\":null},{\"name\":\"cost\",\"type\":\"string\",\"default\":null},{\"name\":\"isHazmat\",\"type\":\"boolean\",\"default\":null},{\"name\":\"shortDescription\",\"type\":\"string\",\"default\":null},{\"name\":\"departmentNumber\",\"type\":\"string\",\"default\":null},{\"name\":\"departmentName\",\"type\":\"string\",\"default\":null},{\"name\":\"imageUrl\",\"type\":\"string\",\"default\":null},{\"name\":\"lastModifiedDate\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}},{\"name\":\"nextdayEligible\",\"type\":\"boolean\",\"default\":null},{\"name\":\"sdndEligibleForDC\",\"type\":\"boolean\",\"default\":null},{\"name\":\"nextdayEligibleForDC\",\"type\":\"boolean\",\"default\":null},{\"name\":\"itemBanner\",\"type\":\"string\",\"default\":null}]}";
    Whitebox.setInternalState(
        PromisingRecord.class, "SCHEMA$", new org.apache.avro.Schema.Parser().parse(schema));

    Builder builder = PromisingRecord.newBuilder();
    Builder result = PromisingRecord.newBuilder(builder);

    Assertions.assertEquals(builder.getBopisEligible(), result.getBopisEligible());
    Assertions.assertEquals(builder.getColor(), result.getColor());
    Assertions.assertEquals(builder.getCost(), result.getCost());
    Assertions.assertEquals(builder.getHeight(), result.getHeight());
    Assertions.assertEquals(builder.getDepartmentName(), result.getDepartmentName());

    PromisingRecord promisingRecord = new PromisingRecord();
    for (int i = 1; i <= 34; i++) {
      if (!(promisingRecord.get(i) instanceof Boolean)) promisingRecord.put(i, null);
    }
    result = PromisingRecord.newBuilder(promisingRecord);
    Assertions.assertEquals(promisingRecord.getBopisEligible(), result.getBopisEligible());
    Assertions.assertEquals(promisingRecord.getColor(), result.getColor());
    Assertions.assertEquals(promisingRecord.getCost(), result.getCost());
    Assertions.assertEquals(promisingRecord.getHeight(), result.getHeight());
    Assertions.assertEquals(promisingRecord.getDepartmentName(), result.getDepartmentName());
  }

  public PromisingRecord getPromiseRecord() {
    PromisingRecord record = new PromisingRecord();
    record.setItemId("item-id");
    record.setOrgId("org-id");
    record.setUom("uom");
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
    record.setWidth(0.0);
    record.setVolume(0.0);
    record.setProcessingTime(1L);
    record.setImageUrl("img.png");
    record.setItemBanner("item-banner");
    record.setVolumeUom("");
    record.setShortDescription("");
    record.setSdndEligible(Boolean.FALSE);
    record.setSdndEligibleForDC(Boolean.FALSE);
    record.setNextdayEligible(Boolean.FALSE);
    record.setNextdayEligibleForDC(Boolean.FALSE);
    return record;
  }
}
