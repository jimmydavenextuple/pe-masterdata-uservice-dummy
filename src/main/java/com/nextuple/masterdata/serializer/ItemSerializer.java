/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.masterdata.serializer;

import jakarta.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Serializer;

@Slf4j
public class ItemSerializer<T extends SpecificRecordBase> implements Serializer<T> {

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    // do nothing
  }

  @Override
  public byte[] serialize(String topic, T payload) {
    byte[] bytes = null;
    try {
      if (payload != null) {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        var binaryEncoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null);
        DatumWriter<SpecificRecordBase> datumWriter =
            new SpecificDatumWriter<>(payload.getSchema());
        datumWriter.write(payload, binaryEncoder);
        binaryEncoder.flush();
        byteArrayOutputStream.close();
        bytes = byteArrayOutputStream.toByteArray();
        log.debug("serialized payload='{}'", DatatypeConverter.printHexBinary(bytes));
      }
    } catch (Exception e) {
      log.error("Unable to serialize payload ", e);
    }
    return bytes;
  }

  @Override
  public void close() {
    // do nothing
  }
}
