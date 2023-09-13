/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.masterdata.serializer;

import com.nextuple.streams.promising.messages.PromisingRecord;
import jakarta.xml.bind.DatatypeConverter;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
public class ItemDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

  @Override
  public void configure(Map configs, boolean isKey) {
    // do nothing
  }

  @SuppressWarnings("unchecked")
  @Override
  public T deserialize(String topic, byte[] bytes) {
    T returnObject = null;

    try {
      if (bytes != null) {
        log.debug("data='{}'", DatatypeConverter.printHexBinary(bytes));

        DatumReader<GenericRecord> datumReader =
            new SpecificDatumReader<>(PromisingRecord.getClassSchema());
        Decoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);

        returnObject = (T) datumReader.read(null, decoder);
        log.debug("deserialized data='{}'", returnObject);
      }
    } catch (Exception e) {
      log.error("Unable to Deserialize bytes[] ", e);
    }

    return returnObject;
  }

  @Override
  public void close() {
    // do nothing
  }
}
